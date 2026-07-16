import Foundation
import Observation

@MainActor
@Observable
final class SessionStore {
    enum State: Equatable { case restoring, signedOut, signedIn }
    private(set) var state: State = .restoring
    private(set) var user: UserProfile?
    private(set) var errorMessage: String?

    private let api: any APIClientProtocol
    private let tokens: any TokenStoring

    init(api: any APIClientProtocol, tokens: any TokenStoring) {
        self.api = api
        self.tokens = tokens
    }

    func restore() async {
        guard (try? await tokens.load()) != nil else { state = .signedOut; return }
        do {
            user = try await api.send(Endpoint(path: "/api/v1/users/me"), as: UserProfile.self)
            state = .signedIn
        } catch {
            try? await tokens.clear()
            state = .signedOut
        }
    }

    func login(email: String, password: String) async -> Bool {
        struct Body: Encodable { let email: String; let password: String }
        do {
            var endpoint = Endpoint(path: "/api/v1/auth/login", method: .post, authenticated: false)
            endpoint.body = try Endpoint.json(Body(email: email, password: password))
            let pair = try await api.send(endpoint, as: TokenPair.self)
            try await tokens.save(pair)
            user = try await api.send(Endpoint(path: "/api/v1/users/me"), as: UserProfile.self)
            state = .signedIn
            errorMessage = nil
            return true
        } catch {
            errorMessage = error.localizedDescription
            return false
        }
    }

    func register(name: String, username: String, email: String, password: String) async -> Bool {
        struct Body: Encodable { let name: String; let username: String; let email: String; let password: String }
        do {
            var endpoint = Endpoint(path: "/api/v1/auth/register", method: .post, authenticated: false)
            endpoint.body = try Endpoint.json(Body(name: name, username: username, email: email, password: password))
            let pair = try await api.send(endpoint, as: TokenPair.self)
            try await tokens.save(pair)
            user = try await api.send(Endpoint(path: "/api/v1/users/me"), as: UserProfile.self)
            state = .signedIn
            errorMessage = nil
            return true
        } catch {
            errorMessage = error.localizedDescription
            return false
        }
    }

    func appleSignIn(_ payload: AppleAuthorizationPayload) async -> Bool {
        do {
            var endpoint = Endpoint(path: "/api/v1/auth/apple", method: .post, authenticated: false)
            endpoint.body = try Endpoint.json(payload)
            let pair = try await api.send(endpoint, as: TokenPair.self)
            try await tokens.save(pair)
            user = try await api.send(Endpoint(path: "/api/v1/users/me"), as: UserProfile.self)
            state = .signedIn
            errorMessage = nil
            return true
        } catch {
            errorMessage = error.localizedDescription
            return false
        }
    }

    func refreshUser() async {
        user = try? await api.send(Endpoint(path: "/api/v1/users/me"), as: UserProfile.self)
    }

    func completeProfile(name: String, username: String) async -> Bool {
        struct Body: Encodable {
            let name: String
            let username: String
            let bio: String
            let avatarUrl: String?
            let publicProfile: Bool
        }
        do {
            var endpoint = Endpoint(path: "/api/v1/users/me", method: .patch)
            endpoint.body = try Endpoint.json(Body(
                name: name,
                username: username,
                bio: user?.bio ?? "",
                avatarUrl: user?.avatarUrl,
                publicProfile: user?.publicProfile ?? true
            ))
            user = try await api.send(endpoint, as: UserProfile.self)
            errorMessage = nil
            return true
        } catch {
            errorMessage = error.localizedDescription
            return false
        }
    }

    func requestPasswordReset(email: String) async -> Bool {
        struct Body: Encodable { let email: String }
        do {
            var endpoint = Endpoint(path: "/api/v1/auth/password-reset/request", method: .post, authenticated: false)
            endpoint.body = try Endpoint.json(Body(email: email))
            try await api.sendVoid(endpoint)
            return true
        } catch {
            errorMessage = error.localizedDescription
            return false
        }
    }

    func requestEmailVerification() async -> Bool {
        do {
            try await api.sendVoid(Endpoint(path: "/api/v1/auth/email-verification/request", method: .post))
            errorMessage = nil
            return true
        } catch {
            errorMessage = error.localizedDescription
            return false
        }
    }

    func handleEmailVerificationURL(_ url: URL) async {
        let isCompletionReturn = url.scheme == "yomora" && url.host == "email-verification"
        let isConfirmationLink = url.path == "/api/v1/auth/email-verification/confirm"
        guard isCompletionReturn || isConfirmationLink else { return }
        do {
            if isConfirmationLink,
               let components = URLComponents(url: url, resolvingAgainstBaseURL: false),
               let token = components.queryItems?.first(where: { $0.name == "token" })?.value {
                struct Body: Encodable { let token: String }
                var endpoint = Endpoint(
                    path: "/api/v1/auth/email-verification/confirm",
                    method: .post,
                    authenticated: false
                )
                endpoint.body = try Endpoint.json(Body(token: token))
                try await api.sendVoid(endpoint)
            }
            await refreshUser()
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    func reportAuthenticationError(_ message: String) {
        errorMessage = message
    }

    func accessMethods() async throws -> AccessMethods {
        try await api.send(Endpoint(path: "/api/v1/users/me/access-methods"), as: AccessMethods.self)
    }

    func linkApple(_ payload: AppleAuthorizationPayload) async -> Bool {
        await performAppleAccessChange(path: "/api/v1/users/me/access-methods/apple", method: .post, payload: payload)
    }

    func unlinkApple(_ payload: AppleAuthorizationPayload) async -> Bool {
        await performAppleAccessChange(path: "/api/v1/users/me/access-methods/apple", method: .delete, payload: payload)
    }

    func addPassword(_ password: String, apple payload: AppleAuthorizationPayload) async -> Bool {
        struct Body: Encodable { let newPassword: String; let apple: AppleAuthorizationPayload }
        do {
            var endpoint = Endpoint(path: "/api/v1/users/me/password", method: .post)
            endpoint.body = try Endpoint.json(Body(newPassword: password, apple: payload))
            try await api.sendVoid(endpoint)
            errorMessage = nil
            return true
        } catch {
            errorMessage = error.localizedDescription
            return false
        }
    }

    func deleteAccount() async -> Bool {
        do {
            try await api.sendVoid(Endpoint(path: "/api/v1/users/me", method: .delete))
            try await tokens.clear()
            user = nil
            state = .signedOut
            errorMessage = nil
            return true
        } catch {
            errorMessage = error.localizedDescription
            return false
        }
    }

    func logout() async {
        if let refresh = try? await tokens.load()?.refreshToken {
            struct Body: Encodable { let refreshToken: String }
            var endpoint = Endpoint(path: "/api/v1/auth/logout", method: .post)
            endpoint.body = try? Endpoint.json(Body(refreshToken: refresh))
            try? await api.sendVoid(endpoint)
        }
        try? await tokens.clear()
        user = nil
        state = .signedOut
    }

    private func performAppleAccessChange(
        path: String,
        method: Endpoint.Method,
        payload: AppleAuthorizationPayload
    ) async -> Bool {
        do {
            var endpoint = Endpoint(path: path, method: method)
            endpoint.body = try Endpoint.json(payload)
            try await api.sendVoid(endpoint)
            errorMessage = nil
            return true
        } catch {
            errorMessage = error.localizedDescription
            return false
        }
    }
}

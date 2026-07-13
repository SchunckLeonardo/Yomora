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

    func refreshUser() async {
        user = try? await api.send(Endpoint(path: "/api/v1/users/me"), as: UserProfile.self)
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
}

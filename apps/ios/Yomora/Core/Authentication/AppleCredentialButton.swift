import AuthenticationServices
import SwiftUI

struct AppleCredentialButton: View {
    let disabled: Bool
    let onCredential: (AppleAuthorizationPayload) async -> Void
    let onError: (String) -> Void

    @Environment(\.colorScheme) private var colorScheme
    @State private var rawNonce: String?
    @State private var working = false

    init(
        disabled: Bool = false,
        onCredential: @escaping (AppleAuthorizationPayload) async -> Void,
        onError: @escaping (String) -> Void
    ) {
        self.disabled = disabled
        self.onCredential = onCredential
        self.onError = onError
    }

    var body: some View {
        SignInWithAppleButton(.continue, onRequest: prepare, onCompletion: complete)
            .signInWithAppleButtonStyle(colorScheme == .dark ? .white : .black)
            .frame(height: 52)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .disabled(disabled || working)
    }

    private func prepare(_ request: ASAuthorizationAppleIDRequest) {
        do {
            let nonce = try AppleSignInNonce.random()
            rawNonce = nonce
            request.nonce = AppleSignInNonce.sha256(nonce)
            request.requestedScopes = [.fullName, .email]
        } catch {
            onError("Não foi possível iniciar o login com a Apple.")
        }
    }

    private func complete(_ result: Result<ASAuthorization, Error>) {
        switch result {
        case let .success(authorization):
            guard
                let credential = authorization.credential as? ASAuthorizationAppleIDCredential,
                let nonce = rawNonce,
                let identityData = credential.identityToken,
                let codeData = credential.authorizationCode,
                let identityToken = String(data: identityData, encoding: .utf8),
                let authorizationCode = String(data: codeData, encoding: .utf8)
            else {
                onError("A Apple não retornou uma credencial válida.")
                return
            }
            let formattedName = credential.fullName.map { PersonNameComponentsFormatter().string(from: $0) }
            let payload = AppleAuthorizationPayload(
                identityToken: identityToken,
                authorizationCode: authorizationCode,
                nonce: nonce,
                fullName: formattedName?.isEmpty == true ? nil : formattedName
            )
            working = true
            Task {
                await onCredential(payload)
                working = false
                rawNonce = nil
            }
        case let .failure(error):
            if (error as? ASAuthorizationError)?.code != .canceled {
                onError(error.localizedDescription)
            }
        }
    }
}

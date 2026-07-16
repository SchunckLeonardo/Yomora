import SwiftUI

struct AccessMethodsView: View {
    let session: SessionStore
    @State private var methods: AccessMethods?
    @State private var newPassword = TextDraft()
    @State private var statusMessage: String?

    var body: some View {
        Form {
            Section("Métodos configurados") {
                LabeledContent("Senha", value: methods?.password == true ? "Ativa" : "Não configurada")
                LabeledContent("Apple", value: methods?.hasApple == true ? "Vinculada" : "Não vinculada")
            }

            if let methods {
                if !methods.hasApple {
                    Section("Vincular Apple") {
                        Text("Use sua conta Apple como uma segunda forma de entrar no Yomora.")
                            .font(.footnote).foregroundStyle(.secondary)
                        appleButton { payload in
                            if await session.linkApple(payload) {
                                statusMessage = "Conta Apple vinculada."
                                await load()
                            }
                        }
                    }
                } else if !methods.password {
                    Section("Adicionar senha") {
                        Text("Confirme sua identidade com a Apple para criar uma senha e manter os dois métodos de acesso.")
                            .font(.footnote).foregroundStyle(.secondary)
                        YomoraDraftField(
                            title: "Nova senha", prompt: "Mínimo de 8 caracteres", draft: newPassword,
                            secure: true, contentType: .newPassword
                        )
                        appleButton(disabled: newPassword.value.count < 8) { payload in
                            if await session.addPassword(newPassword.value, apple: payload) {
                                newPassword.value = ""
                                statusMessage = "Senha adicionada."
                                await load()
                            }
                        }
                    }
                } else {
                    Section("Desvincular Apple") {
                        Text("Sua senha continuará ativa. A Apple pedirá uma confirmação antes de remover o vínculo.")
                            .font(.footnote).foregroundStyle(.secondary)
                        appleButton { payload in
                            if await session.unlinkApple(payload) {
                                statusMessage = "Conta Apple desvinculada."
                                await load()
                            }
                        }
                    }
                }
            }

            if let statusMessage {
                Section { Text(statusMessage).font(.footnote).foregroundStyle(.secondary) }
            } else if let error = session.errorMessage {
                Section { Text(error).font(.footnote).foregroundStyle(YomoraColor.danger) }
            }
        }
        .navigationTitle("Métodos de acesso")
        .task { await load() }
    }

    @ViewBuilder
    private func appleButton(
        disabled: Bool = false,
        action: @escaping (AppleAuthorizationPayload) async -> Void
    ) -> some View {
        AppleCredentialButton(disabled: disabled, onCredential: action) { message in
            session.reportAuthenticationError(message)
        }
    }

    private func load() async {
        methods = try? await session.accessMethods()
    }
}

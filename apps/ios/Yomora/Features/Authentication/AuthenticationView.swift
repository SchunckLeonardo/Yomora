import SwiftUI

struct AuthenticationView: View {
    @Bindable var session: SessionStore
    @State private var mode: Mode = .login
    @State private var name = TextDraft()
    @State private var username = TextDraft()
    @State private var email = TextDraft()
    @State private var password = TextDraft()
    @State private var busy = false
    @State private var showingReset = false

    enum Mode: String, CaseIterable { case login = "Entrar", register = "Criar conta" }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 22) {
                    Image("Logo").resizable().scaledToFit().frame(width: 110, height: 110)
                    Text(mode == .login ? "Que bom ter você de volta" : "Sua próxima leitura começa aqui")
                        .font(.yomoraTitle).multilineTextAlignment(.center).foregroundStyle(YomoraColor.primary)
                    Picker("Acesso", selection: $mode) {
                        ForEach(Mode.allCases, id: \.self) { Text($0.rawValue).tag($0) }
                    }.pickerStyle(.segmented)
                    if mode == .register {
                        YomoraDraftField(title: "Nome", prompt: "Como podemos chamar você?", draft: name)
                        YomoraDraftField(title: "Nome de usuário", prompt: "seu.nome", draft: username, contentType: .username)
                    }
                    YomoraDraftField(
                        title: "E-mail",
                        prompt: "voce@exemplo.com",
                        draft: email,
                        keyboard: .emailAddress,
                        contentType: mode == .login ? .username : .emailAddress
                    )
                        .accessibilityIdentifier("emailField")
                    YomoraDraftField(
                        title: "Senha",
                        prompt: "Mínimo de 8 caracteres",
                        draft: password,
                        secure: true,
                        contentType: mode == .login ? .password : .newPassword
                    )
                        .accessibilityIdentifier("passwordField")
                    if let message = session.errorMessage {
                        Text(message).font(.footnote).foregroundStyle(YomoraColor.danger).frame(maxWidth: .infinity, alignment: .leading)
                    }
                    PrimaryButton(title: mode.rawValue, isLoading: busy) {
                        Task { await submit() }
                    }
                    .accessibilityIdentifier("authenticationButton")
                    HStack {
                        Rectangle().frame(height: 1).foregroundStyle(YomoraColor.outline)
                        Text("ou").font(.footnote).foregroundStyle(YomoraColor.textSecondary)
                        Rectangle().frame(height: 1).foregroundStyle(YomoraColor.outline)
                    }
                    AppleCredentialButton(disabled: busy) { payload in
                        busy = true
                        _ = await session.appleSignIn(payload)
                        busy = false
                    } onError: { message in
                        session.reportAuthenticationError(message)
                    }
                        .accessibilityIdentifier("appleSignInButton")
                    if mode == .login {
                        Button("Esqueci minha senha") { showingReset = true }
                    }
                }
                .padding(24)
            }
            .background(YomoraColor.canvas)
            .sheet(isPresented: $showingReset) { PasswordResetView(session: session) }
        }
    }

    @MainActor
    private func submit() async {
        guard !email.isBlank, !password.isBlank else { return }
        busy = true
        if mode == .login {
            _ = await session.login(email: email.value, password: password.value)
        } else {
            _ = await session.register(
                name: name.value,
                username: username.value,
                email: email.value,
                password: password.value
            )
        }
        busy = false
    }
}

private struct PasswordResetView: View {
    let session: SessionStore
    @Environment(\.dismiss) private var dismiss
    @State private var email = TextDraft()
    var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 20) {
                Text("Recuperar acesso").font(.yomoraTitle)
                Text("Enviaremos as instruções se houver uma conta com este e-mail.").foregroundStyle(.secondary)
                YomoraDraftField(title: "E-mail", draft: email, keyboard: .emailAddress, contentType: .emailAddress)
                PrimaryButton(title: "Solicitar recuperação") {
                    Task { if await session.requestPasswordReset(email: email.value) { dismiss() } }
                }
                Spacer()
            }
            .padding()
            .toolbar { ToolbarItem(placement: .cancellationAction) { Button("Fechar") { dismiss() } } }
        }
    }
}

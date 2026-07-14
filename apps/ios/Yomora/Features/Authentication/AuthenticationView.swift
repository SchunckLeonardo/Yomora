import SwiftUI

struct AuthenticationView: View {
    @Bindable var session: SessionStore
    @State private var mode: Mode = .login
    @State private var name = ""
    @State private var username = ""
    @State private var email = ""
    @State private var password = ""
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
                        YomoraTextField(title: "Nome", prompt: "Como podemos chamar você?", text: $name)
                        YomoraTextField(title: "Nome de usuário", prompt: "seu.nome", text: $username)
                    }
                    YomoraTextField(title: "E-mail", prompt: "voce@exemplo.com", text: $email, keyboard: .emailAddress)
                        .accessibilityIdentifier("emailField")
                    YomoraTextField(title: "Senha", prompt: "Mínimo de 8 caracteres", text: $password, secure: true)
                        .accessibilityIdentifier("passwordField")
                    if let message = session.errorMessage {
                        Text(message).font(.footnote).foregroundStyle(YomoraColor.danger).frame(maxWidth: .infinity, alignment: .leading)
                    }
                    PrimaryButton(title: mode.rawValue, isLoading: busy) {
                        Task { await submit() }
                    }
                    .accessibilityIdentifier("authenticationButton")
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
        guard !email.isEmpty, !password.isEmpty else { return }
        busy = true
        if mode == .login { _ = await session.login(email: email, password: password) }
        else { _ = await session.register(name: name, username: username, email: email, password: password) }
        busy = false
    }
}

private struct PasswordResetView: View {
    let session: SessionStore
    @Environment(\.dismiss) private var dismiss
    @State private var email = ""
    var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 20) {
                Text("Recuperar acesso").font(.yomoraTitle)
                Text("Enviaremos as instruções se houver uma conta com este e-mail.").foregroundStyle(.secondary)
                YomoraTextField(title: "E-mail", text: $email, keyboard: .emailAddress)
                PrimaryButton(title: "Solicitar recuperação") { Task { if await session.requestPasswordReset(email: email) { dismiss() } } }
                Spacer()
            }
            .padding()
            .toolbar { ToolbarItem(placement: .cancellationAction) { Button("Fechar") { dismiss() } } }
        }
    }
}

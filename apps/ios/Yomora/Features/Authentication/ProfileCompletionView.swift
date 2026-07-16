import SwiftUI

struct ProfileCompletionView: View {
    let session: SessionStore
    @State private var name: TextDraft
    @State private var username = TextDraft()
    @State private var busy = false

    init(session: SessionStore) {
        self.session = session
        _name = State(initialValue: TextDraft(session.user?.name ?? ""))
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: YomoraSpacing.lg) {
                    Image("Logo").resizable().scaledToFit().frame(width: 88, height: 88)
                    Text("Complete seu perfil").font(.yomoraTitle).foregroundStyle(YomoraColor.primary)
                    Text("A Apple confirmou seu acesso. Escolha como seu nome aparecerá para outros leitores.")
                        .foregroundStyle(YomoraColor.textSecondary)
                    YomoraDraftField(title: "Nome", prompt: "Como podemos chamar você?", draft: name, contentType: .name)
                    YomoraDraftField(title: "Nome de usuário", prompt: "seu.nome", draft: username, contentType: .username)
                    if let message = session.errorMessage {
                        Text(message).font(.footnote).foregroundStyle(YomoraColor.danger)
                    }
                    PrimaryButton(title: "Continuar", isLoading: busy) {
                        Task { await save() }
                    }
                    .disabled(name.isBlank || username.isBlank)
                    Button("Sair desta conta", role: .cancel) { Task { await session.logout() } }
                        .frame(maxWidth: .infinity)
                }
                .padding(24)
            }
            .background(YomoraColor.canvas)
        }
    }

    @MainActor
    private func save() async {
        busy = true
        _ = await session.completeProfile(name: name.value, username: username.value)
        busy = false
    }
}

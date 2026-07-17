import SwiftUI

struct PostComposerView: View {
    let api: any APIClientProtocol
    @Environment(\.dismiss) private var dismiss
    @State private var text = TextDraft()
    @State private var type: PostType = .recommendation
    @State private var visibility: PostVisibility = .publicPost
    @State private var spoiler = false
    @State private var publishing = false
    @State private var error: String?
    @State private var linkedEditionId: UUID?

    init(api: any APIClientProtocol, editionId: UUID? = nil) {
        self.api = api
        _linkedEditionId = State(initialValue: editionId)
    }

    var body: some View {
        Form {
            Section("O que você quer compartilhar?") {
                Picker("Tipo", selection: $type) { ForEach(PostType.allCases, id: \.self) { Text(title($0)).tag($0) } }
                DraftTextField(prompt: prompt(for: type), draft: text, axis: .vertical)
                    .lineLimit(5...12)
                Toggle("Contém spoiler", isOn: $spoiler)
                Picker("Visibilidade", selection: $visibility) { ForEach(PostVisibility.allCases, id: \.self) { Text($0.rawValue.capitalized).tag($0) } }
            }
            Section("Modelo rápido") {
                Button {
                    if text.isBlank { text.value = template(for: type) }
                } label: {
                    Label("Começar com um modelo de \(title(type).lowercased())", systemImage: "wand.and.stars")
                }
                if linkedEditionId != nil {
                    HStack {
                        Label("Livro vinculado", systemImage: "book.closed.fill")
                        Spacer()
                        Button("Remover") { linkedEditionId = nil }
                    }
                } else {
                    Text("O vínculo com um livro é opcional e pode ser iniciado na tela de detalhes dele.")
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }
            }
            if let error { Text(error).foregroundStyle(YomoraColor.danger) }
        }
        .navigationTitle("Nova publicação")
        .toolbar {
            ToolbarItem(placement: .cancellationAction) { Button("Cancelar") { dismiss() } }
            ToolbarItem(placement: .confirmationAction) {
                DraftActionButton(title: publishing ? "Publicando…" : "Publicar", draft: text, isBusy: publishing) {
                    Task { await publish() }
                }
                .accessibilityIdentifier("publishButton")
            }
        }
    }

    private func publish() async {
        guard !text.isBlank else { return }
        struct Body: Encodable { let text: String; let editionId: UUID?; let type: PostType; let spoiler: Bool; let spoilerPage: Int?; let visibility: PostVisibility }
        publishing = true
        do {
            var endpoint = Endpoint(path: "/api/v1/posts", method: .post)
            endpoint.body = try Endpoint.json(Body(text: text.value, editionId: linkedEditionId, type: type, spoiler: spoiler, spoilerPage: nil, visibility: visibility))
            _ = try await api.send(endpoint, as: Post.self)
            dismiss()
        } catch { self.error = error.localizedDescription; publishing = false }
    }

    private func title(_ type: PostType) -> String {
        switch type { case .note: "Nota"; case .review: "Review"; case .recommendation: "Recomendação"; case .progress: "Progresso"; case .quote: "Citação" }
    }

    private func prompt(for type: PostType) -> String {
        switch type {
        case .note: "O que ficou desta leitura?"
        case .review: "Como foi sua experiência com o livro?"
        case .recommendation: "Para quem você recomendaria esta leitura?"
        case .progress: "Como está avançando sua leitura?"
        case .quote: "Compartilhe a citação e o que ela despertou."
        }
    }

    private func template(for type: PostType) -> String {
        switch type {
        case .note: "Uma ideia que quero guardar desta leitura: "
        case .review: "O que mais funcionou para mim neste livro foi…\n\nO que poderia ser melhor…"
        case .recommendation: "Recomendo este livro para quem… porque…"
        case .progress: "Cheguei até aqui e, por enquanto, a leitura está…"
        case .quote: "“”\n\nEsta passagem me fez pensar em…"
        }
    }
}

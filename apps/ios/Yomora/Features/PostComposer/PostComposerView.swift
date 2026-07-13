import SwiftUI

struct PostComposerView: View {
    let api: any APIClientProtocol
    @Environment(\.dismiss) private var dismiss
    @State private var text = ""
    @State private var type: PostType = .recommendation
    @State private var visibility: PostVisibility = .publicPost
    @State private var spoiler = false
    @State private var publishing = false
    @State private var error: String?

    var body: some View {
        Form {
            Section("O que você quer compartilhar?") {
                Picker("Tipo", selection: $type) { ForEach(PostType.allCases, id: \.self) { Text(title($0)).tag($0) } }
                TextField("Conte sobre a leitura…", text: $text, axis: .vertical).lineLimit(5...12)
                Toggle("Contém spoiler", isOn: $spoiler)
                Picker("Visibilidade", selection: $visibility) { ForEach(PostVisibility.allCases, id: \.self) { Text($0.rawValue.capitalized).tag($0) } }
            }
            if let error { Text(error).foregroundStyle(YomoraColor.danger) }
        }
        .navigationTitle("Nova publicação")
        .toolbar {
            ToolbarItem(placement: .cancellationAction) { Button("Cancelar") { dismiss() } }
            ToolbarItem(placement: .confirmationAction) { Button(publishing ? "Publicando…" : "Publicar") { Task { await publish() } }.disabled(text.isEmpty || publishing).accessibilityIdentifier("publishButton") }
        }
    }

    private func publish() async {
        struct Body: Encodable { let text: String; let editionId: UUID?; let type: PostType; let spoiler: Bool; let spoilerPage: Int?; let visibility: PostVisibility }
        publishing = true
        do {
            var endpoint = Endpoint(path: "/api/v1/posts", method: .post)
            endpoint.body = try Endpoint.json(Body(text: text, editionId: nil, type: type, spoiler: spoiler, spoilerPage: nil, visibility: visibility))
            _ = try await api.send(endpoint, as: Post.self)
            dismiss()
        } catch { self.error = error.localizedDescription; publishing = false }
    }

    private func title(_ type: PostType) -> String {
        switch type { case .note: "Nota"; case .review: "Review"; case .recommendation: "Recomendação"; case .progress: "Progresso"; case .quote: "Citação" }
    }
}


import SwiftUI

struct BookDetailsView: View {
    let book: Book
    let api: any APIClientProtocol
    @State private var status: ReadingStatus = .wantToRead
    @State private var adding = false
    @State private var added: LibraryBook?
    @State private var errorMessage: String?
    @State private var showingComposer = false

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 22) {
                HStack(alignment: .top, spacing: 20) {
                    BookCover(url: book.coverUrl, width: 120, height: 178)
                    VStack(alignment: .leading, spacing: 10) {
                        Text(book.title).font(.yomoraTitle)
                        Text(book.authorLine).foregroundStyle(.secondary)
                        if let pages = book.pageCount { Label("\(pages) páginas", systemImage: "book.pages") }
                        ShareLink(item: URL(string: "https://yomora.app/books/\(book.editionId)")!) {
                            Label("Compartilhar", systemImage: "square.and.arrow.up")
                        }
                        Button { showingComposer = true } label: {
                            Label("Publicar sobre o livro", systemImage: "text.bubble")
                        }
                    }
                }
                if !book.description.isEmpty {
                    Text("Sobre o livro").font(.yomoraHeading)
                    Text(book.description).font(.yomoraEditorial)
                }
                Picker("Status", selection: $status) {
                    ForEach(ReadingStatus.allCases, id: \.self) { Text($0.title).tag($0) }
                }.pickerStyle(.menu)
                if let errorMessage { Text(errorMessage).foregroundStyle(YomoraColor.danger) }
                if added != nil {
                    Label("Adicionado à sua biblioteca", systemImage: "checkmark.circle.fill")
                        .foregroundStyle(YomoraColor.sereneTeal).font(.headline)
                } else {
                    PrimaryButton(title: "Adicionar à biblioteca", systemImage: "plus", isLoading: adding) {
                        Task { await add() }
                    }.accessibilityIdentifier("addToLibraryButton")
                }
            }.padding()
        }
        .navigationTitle("Detalhes")
        .navigationBarTitleDisplayMode(.inline)
        .background(YomoraColor.canvas)
        .sheet(isPresented: $showingComposer) {
            NavigationStack { PostComposerView(api: api, editionId: book.editionId) }
        }
    }

    @MainActor
    private func add() async {
        struct Body: Encodable { let editionId: UUID; let status: ReadingStatus; let targetFinishDate: String? }
        adding = true
        do {
            var endpoint = Endpoint(path: "/api/v1/library/books", method: .post)
            endpoint.body = try Endpoint.json(Body(editionId: book.editionId, status: status, targetFinishDate: nil))
            added = try await api.send(endpoint, as: LibraryBook.self)
            errorMessage = nil
        } catch { errorMessage = error.localizedDescription }
        adding = false
    }
}

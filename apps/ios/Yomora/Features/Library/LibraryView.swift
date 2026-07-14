import SwiftUI

struct LibraryView: View {
    let container: AppContainer
    @State private var viewModel: LibraryViewModel
    @State private var showingShelves = false

    init(container: AppContainer) {
        self.container = container
        _viewModel = State(initialValue: LibraryViewModel(api: container.api))
    }

    var body: some View {
        VStack(spacing: 0) {
            Picker("Status", selection: $viewModel.filter) {
                Text("Todos").tag(ReadingStatus?.none)
                ForEach(ReadingStatus.allCases, id: \.self) { Text($0.title).tag(Optional($0)) }
            }
            .pickerStyle(.menu).frame(maxWidth: .infinity, alignment: .leading).padding(.horizontal)
            Group {
                switch viewModel.state {
                case .idle, .loading: ScrollView { LazyVStack { ForEach(0..<4, id: \.self) { _ in LoadingSkeleton() } }.padding() }
                case let .error(message): ErrorStateView(message: message) { Task { await viewModel.load() } }
                case .loaded:
                    if viewModel.items.isEmpty { EmptyStateView(title: "Sua biblioteca está esperando", message: "Busque uma edição e adicione sua primeira leitura.") }
                    else {
                        ScrollView {
                            LazyVGrid(columns: [GridItem(.adaptive(minimum: 150), spacing: 16)], spacing: 22) {
                                ForEach(viewModel.items) { item in
                                    NavigationLink(value: AppRoute.libraryBook(item.entry)) {
                                        VStack(alignment: .leading, spacing: 8) {
                                            BookCover(url: item.book.coverUrl, width: 142, height: 205)
                                            Text(item.book.title).font(.headline).lineLimit(2)
                                            Text(item.entry.status.title).font(.caption).foregroundStyle(YomoraColor.sereneTeal)
                                        }
                                    }.buttonStyle(.plain)
                                }
                            }.padding()
                        }
                    }
                }
            }
        }
        .navigationTitle("Biblioteca")
        .toolbar { ToolbarItem(placement: .topBarTrailing) { Button { showingShelves = true } label: { Label("Estantes", systemImage: "square.grid.2x2") } } }
        .sheet(isPresented: $showingShelves) { NavigationStack { ShelfListView(api: container.api) } }
        .task(id: viewModel.filter) { await viewModel.load() }
        .refreshable { await viewModel.load() }
        .background(YomoraColor.canvas)
    }
}

struct LibraryBookDetailsView: View {
    let entry: LibraryBook
    let container: AppContainer
    @State private var book: Book?
    @State private var page: Int
    @State private var status: ReadingStatus
    @State private var rating = 0
    @State private var saving = false
    @State private var writingMode: BookWritingView.Mode?

    init(entry: LibraryBook, container: AppContainer) {
        self.entry = entry; self.container = container
        _page = State(initialValue: entry.currentPage); _status = State(initialValue: entry.status)
        _rating = State(initialValue: entry.rating ?? 0)
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 22) {
                if let book { BookCard(book: book, progress: book.pageCount.map { Double(page) / Double(max($0, 1)) }) }
                Stepper("Página atual: \(page)", value: $page, in: 0...(book?.pageCount ?? 10_000))
                Picker("Status", selection: $status) { ForEach(ReadingStatus.allCases, id: \.self) { Text($0.title).tag($0) } }
                RatingView(rating: $rating)
                PrimaryButton(title: "Salvar progresso", isLoading: saving) { Task { await save() } }
                if book != nil {
                    HStack {
                        Button { writingMode = .note } label: { Label("Criar nota", systemImage: "note.text") }.buttonStyle(.bordered)
                        Button { writingMode = .review } label: { Label("Escrever review", systemImage: "star.bubble") }.buttonStyle(.bordered)
                    }
                }
                if status == .reading {
                    NavigationLink(value: AppRoute.reading(entry, title: book?.title ?? "Sua leitura")) {
                        Label("Iniciar sessão de leitura", systemImage: "timer")
                            .font(.headline).frame(maxWidth: .infinity, minHeight: 52).foregroundStyle(YomoraColor.onInteractive)
                            .background(YomoraColor.interactiveFill, in: RoundedRectangle(cornerRadius: YomoraRadius.button))
                    }.accessibilityIdentifier("startReadingSessionButton")
                }
                if let book {
                    ShareLink(item: URL(string: "https://yomora.app/books/\(book.editionId)")!) { Label("Compartilhar livro", systemImage: "square.and.arrow.up") }
                }
            }.padding()
        }
        .navigationTitle("Minha leitura").navigationBarTitleDisplayMode(.inline)
        .task { book = try? await container.api.send(Endpoint(path: "/api/v1/books/\(entry.editionId)", authenticated: false), as: Book.self) }
        .sheet(item: $writingMode) { mode in
            if let book { NavigationStack { BookWritingView(mode: mode, book: book, page: page, api: container.api) } }
        }
    }

    private func save() async {
        struct Body: Encodable { let status: ReadingStatus; let currentPage: Int; let rating: Int?; let targetFinishDate: String? }
        saving = true
        var endpoint = Endpoint(path: "/api/v1/library/books/\(entry.id)", method: .patch)
        endpoint.body = try? Endpoint.json(Body(status: status, currentPage: page, rating: rating == 0 ? nil : rating, targetFinishDate: entry.targetFinishDate))
        _ = try? await container.api.send(endpoint, as: LibraryBook.self)
        saving = false
    }
}

private struct BookWritingView: View {
    enum Mode: String, Identifiable { case note, review; var id: String { rawValue } }
    let mode: Mode
    let book: Book
    let page: Int
    let api: any APIClientProtocol
    @Environment(\.dismiss) private var dismiss
    @State private var title = ""
    @State private var text = ""
    @State private var rating = 4
    @State private var privateNote = true
    @State private var spoiler = false

    var body: some View {
        Form {
            Section(book.title) {
                if mode == .review { RatingView(rating: $rating); TextField("Título opcional", text: $title) }
                TextField(mode == .note ? "Sua nota" : "O que você achou?", text: $text, axis: .vertical).lineLimit(5...12)
                if mode == .note { Toggle("Manter privada", isOn: $privateNote) }
                Toggle("Contém spoiler", isOn: $spoiler)
            }
        }
        .navigationTitle(mode == .note ? "Nova nota" : "Nova review")
        .toolbar {
            ToolbarItem(placement: .cancellationAction) { Button("Cancelar") { dismiss() } }
            ToolbarItem(placement: .confirmationAction) { Button("Publicar") { Task { await submit() } }.disabled(text.isEmpty) }
        }
    }

    private func submit() async {
        if mode == .note {
            struct Body: Encodable { let editionId: UUID; let content: String; let page: Int?; let chapter: String?; let privateNote: Bool; let spoiler: Bool }
            var endpoint = Endpoint(path: "/api/v1/notes", method: .post)
            endpoint.body = try? Endpoint.json(Body(editionId: book.editionId, content: text, page: page, chapter: nil, privateNote: privateNote, spoiler: spoiler))
            if (try? await api.send(endpoint, as: Note.self)) != nil { dismiss() }
        } else {
            struct Body: Encodable { let workId: UUID; let editionId: UUID; let rating: Int; let title: String?; let text: String; let spoiler: Bool }
            var endpoint = Endpoint(path: "/api/v1/reviews", method: .post)
            endpoint.body = try? Endpoint.json(Body(workId: book.workId, editionId: book.editionId, rating: rating, title: title.isEmpty ? nil : title, text: text, spoiler: spoiler))
            if (try? await api.send(endpoint, as: Review.self)) != nil { dismiss() }
        }
    }
}

private struct ShelfListView: View {
    let api: any APIClientProtocol
    @Environment(\.dismiss) private var dismiss
    @State private var shelves: [Shelf] = []
    @State private var name = ""
    var body: some View {
        List {
            Section("Nova estante") {
                TextField("Ex.: Ler em 2026", text: $name)
                Button("Criar") { Task { await create() } }.disabled(name.trimmingCharacters(in: .whitespaces).isEmpty)
            }
            Section("Suas estantes") {
                ForEach(shelves) { shelf in Label(shelf.name, systemImage: shelf.publicShelf ? "globe" : "lock") }
            }
        }
        .navigationTitle("Estantes")
        .toolbar { ToolbarItem(placement: .confirmationAction) { Button("Fechar") { dismiss() } } }
        .task { shelves = (try? await api.send(Endpoint(path: "/api/v1/shelves"), as: [Shelf].self)) ?? [] }
    }

    private func create() async {
        struct Body: Encodable { let name: String; let publicShelf: Bool }
        var endpoint = Endpoint(path: "/api/v1/shelves", method: .post)
        endpoint.body = try? Endpoint.json(Body(name: name, publicShelf: false))
        if let shelf = try? await api.send(endpoint, as: Shelf.self) { shelves.append(shelf); name = "" }
    }
}

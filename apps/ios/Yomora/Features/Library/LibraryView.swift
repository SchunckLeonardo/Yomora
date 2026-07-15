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
        .onReceive(NotificationCenter.default.publisher(for: .libraryDidChange)) { _ in
            Task { await viewModel.load() }
        }
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
    @State private var saveError: String?
    @State private var sessionNotes: [ReadingSession] = []
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
                if let saveError {
                    Text(saveError)
                        .font(.footnote)
                        .foregroundStyle(.red)
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
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
                if !sessionNotes.isEmpty {
                    VStack(alignment: .leading, spacing: YomoraSpacing.md) {
                        Text("Notas das sessões").font(.yomoraHeading)
                        ForEach(sessionNotes) { session in
                            VStack(alignment: .leading, spacing: YomoraSpacing.sm) {
                                if let note = session.note {
                                    Text(note)
                                        .font(.body)
                                        .accessibilityIdentifier("sessionNote-\(session.id)")
                                }
                                Text(pageDescription(for: session))
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding()
                            .background(YomoraColor.surface, in: RoundedRectangle(cornerRadius: YomoraRadius.card))
                            .overlay {
                                RoundedRectangle(cornerRadius: YomoraRadius.card)
                                    .stroke(YomoraColor.outline.opacity(0.5))
                            }
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                }
            }.padding()
        }
        .navigationTitle("Minha leitura").navigationBarTitleDisplayMode(.inline)
        .task { await loadDetails() }
        .sheet(item: $writingMode) { mode in
            if let book { NavigationStack { BookWritingView(mode: mode, book: book, page: page, api: container.api) } }
        }
    }

    private func save() async {
        struct Body: Encodable { let status: ReadingStatus; let currentPage: Int; let rating: Int?; let targetFinishDate: String? }
        saving = true
        saveError = nil
        defer { saving = false }
        do {
            var endpoint = Endpoint(path: "/api/v1/library/books/\(entry.id)", method: .patch)
            endpoint.body = try Endpoint.json(Body(
                status: status,
                currentPage: page,
                rating: rating == 0 ? nil : rating,
                targetFinishDate: entry.targetFinishDate
            ))
            let saved = try await container.api.send(endpoint, as: LibraryBook.self)
            status = saved.status
            page = saved.currentPage
            rating = saved.rating ?? 0
            NotificationCenter.default.post(name: .libraryDidChange, object: nil)
        } catch {
            saveError = "Não foi possível salvar: \(error.localizedDescription)"
        }
    }

    private func loadDetails() async {
        async let loadedBook = try? container.api.send(
            Endpoint(path: "/api/v1/books/\(entry.editionId)", authenticated: false),
            as: Book.self
        )
        async let loadedSessions = try? container.api.send(
            Endpoint(path: "/api/v1/reading-sessions"),
            as: [ReadingSession].self
        )
        book = await loadedBook
        let sessions = await loadedSessions ?? []
        sessionNotes = sessions.filter { session in
            session.userBookId == entry.id
                && !(session.note?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ?? true)
        }
        .sorted { $0.startedAt > $1.startedAt }
    }

    private func pageDescription(for session: ReadingSession) -> String {
        guard let endPage = session.endPage else { return "Iniciada na página \(session.startPage)" }
        return "Páginas \(session.startPage)–\(endPage)"
    }
}

private struct BookWritingView: View {
    enum Mode: String, Identifiable { case note, review; var id: String { rawValue } }
    let mode: Mode
    let book: Book
    let page: Int
    let api: any APIClientProtocol
    @Environment(\.dismiss) private var dismiss
    @State private var title = TextDraft()
    @State private var text = TextDraft()
    @State private var rating = 4
    @State private var privateNote = true
    @State private var spoiler = false

    var body: some View {
        Form {
            Section(book.title) {
                if mode == .review {
                    RatingView(rating: $rating)
                    DraftTextField(prompt: "Título opcional", draft: title)
                }
                DraftTextField(prompt: mode == .note ? "Sua nota" : "O que você achou?", draft: text, axis: .vertical)
                    .lineLimit(5...12)
                if mode == .note { Toggle("Manter privada", isOn: $privateNote) }
                Toggle("Contém spoiler", isOn: $spoiler)
            }
        }
        .navigationTitle(mode == .note ? "Nova nota" : "Nova review")
        .toolbar {
            ToolbarItem(placement: .cancellationAction) { Button("Cancelar") { dismiss() } }
            ToolbarItem(placement: .confirmationAction) {
                DraftActionButton(title: "Publicar", draft: text) { Task { await submit() } }
            }
        }
    }

    private func submit() async {
        if mode == .note {
            struct Body: Encodable { let editionId: UUID; let content: String; let page: Int?; let chapter: String?; let privateNote: Bool; let spoiler: Bool }
            var endpoint = Endpoint(path: "/api/v1/notes", method: .post)
            endpoint.body = try? Endpoint.json(Body(editionId: book.editionId, content: text.value, page: page, chapter: nil, privateNote: privateNote, spoiler: spoiler))
            if (try? await api.send(endpoint, as: Note.self)) != nil { dismiss() }
        } else {
            struct Body: Encodable { let workId: UUID; let editionId: UUID; let rating: Int; let title: String?; let text: String; let spoiler: Bool }
            var endpoint = Endpoint(path: "/api/v1/reviews", method: .post)
            endpoint.body = try? Endpoint.json(Body(
                workId: book.workId,
                editionId: book.editionId,
                rating: rating,
                title: title.value.isEmpty ? nil : title.value,
                text: text.value,
                spoiler: spoiler
            ))
            if (try? await api.send(endpoint, as: Review.self)) != nil { dismiss() }
        }
    }
}

private struct ShelfListView: View {
    let api: any APIClientProtocol
    @Environment(\.dismiss) private var dismiss
    @State private var shelves: [Shelf] = []
    @State private var name = TextDraft()
    var body: some View {
        List {
            Section("Nova estante") {
                DraftTextField(prompt: "Ex.: Ler em 2026", draft: name)
                DraftActionButton(title: "Criar", draft: name) { Task { await create() } }
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
        endpoint.body = try? Endpoint.json(Body(name: name.value, publicShelf: false))
        if let shelf = try? await api.send(endpoint, as: Shelf.self) { shelves.append(shelf); name.value = "" }
    }
}

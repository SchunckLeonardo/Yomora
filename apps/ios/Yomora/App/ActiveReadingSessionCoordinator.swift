import Foundation
import Observation

struct ActiveReadingSessionContext: Sendable, Equatable {
    let session: ReadingSession
    let entry: LibraryBook
    let book: Book
}

@MainActor
@Observable
final class ActiveReadingSessionCoordinator {
    private(set) var context: ActiveReadingSessionContext?
    private(set) var isLoading = false
    private(set) var errorMessage: String?

    private let api: any APIClientProtocol
    private let activity: any ReadingActivityManaging

    init(
        api: any APIClientProtocol,
        activity: any ReadingActivityManaging = NoopReadingActivityManager()
    ) {
        self.api = api
        self.activity = activity
    }

    func restore() async {
        guard !isLoading else { return }
        isLoading = true
        defer { isLoading = false }
        do {
            guard let session = try await api.sendOptional(
                Endpoint(path: "/api/v1/reading-sessions/active"),
                as: ReadingSession.self
            ) else {
                context = nil
                errorMessage = nil
                return
            }
            let entries = try await api.send(Endpoint(path: "/api/v1/library"), as: [LibraryBook].self)
            guard let entry = entries.first(where: { $0.id == session.userBookId }) else {
                throw APIError.server(status: 404, message: "O livro da sessão ativa não está mais na biblioteca.")
            }
            let book = try await api.send(
                Endpoint(path: "/api/v1/books/\(entry.editionId)", authenticated: false),
                as: Book.self
            )
            context = ActiveReadingSessionContext(session: session, entry: entry, book: book)
            if let snapshot = ReadingActivitySnapshot(session: session, bookTitle: book.title) {
                await activity.start(snapshot)
            }
            errorMessage = nil
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    func set(session: ReadingSession, entry: LibraryBook, book: Book) {
        context = ActiveReadingSessionContext(session: session, entry: entry, book: book)
        errorMessage = nil
    }

    func update(_ session: ReadingSession) {
        guard let context, context.session.id == session.id else { return }
        self.context = ActiveReadingSessionContext(session: session, entry: context.entry, book: context.book)
    }

    func clear() {
        context = nil
        errorMessage = nil
    }
}

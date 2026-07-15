import XCTest
@testable import Yomora

@MainActor
final class ActiveReadingSessionCoordinatorTests: XCTestCase {
    func testRestoreResolvesTheActiveSessionLibraryEntryAndBook() async {
        let userId = UUID()
        let entry = LibraryBook(
            id: UUID(), userId: userId, editionId: UUID(), status: .reading, currentPage: 42,
            startedAt: "2026-07-01T10:00:00Z", finishedAt: nil, rating: nil,
            targetFinishDate: nil, createdAt: "2026-07-01T10:00:00Z", updatedAt: "2026-07-01T10:00:00Z"
        )
        let session = ReadingSession(
            id: UUID(), userId: userId, userBookId: entry.id, startPage: 40, currentPage: 47,
            endPage: nil, goalPages: nil, startedAt: "2026-07-15T10:00:00Z",
            pausedAt: nil, pausedSeconds: 300, finishedAt: nil, durationSeconds: nil,
            pagesRead: nil, note: nil
        )
        let book = Book(
            workId: UUID(), editionId: entry.editionId, title: "Uma leitura constante",
            description: "", authors: ["Autora Yomora"], categories: [], isbn10: nil,
            isbn13: nil, publisher: nil, publicationDate: nil, language: "pt",
            pageCount: 180, coverUrl: nil, externalProvider: nil, externalId: nil
        )
        let api = ActiveSessionAPI(session: session, entry: entry, book: book)
        let coordinator = ActiveReadingSessionCoordinator(api: api)

        await coordinator.restore()

        XCTAssertEqual(coordinator.context?.session, session)
        XCTAssertEqual(coordinator.context?.entry, entry)
        XCTAssertEqual(coordinator.context?.book, book)
        XCTAssertNil(coordinator.errorMessage)
        let paths = await api.requestedPaths()
        XCTAssertEqual(
            paths,
            ["/api/v1/reading-sessions/active", "/api/v1/library", "/api/v1/books/\(entry.editionId)"]
        )
    }

    func testRestoreClearsContextWhenThereIsNoActiveSession() async {
        let api = ActiveSessionAPI(session: nil, entry: nil, book: nil)
        let coordinator = ActiveReadingSessionCoordinator(api: api)

        await coordinator.restore()

        XCTAssertNil(coordinator.context)
        XCTAssertNil(coordinator.errorMessage)
    }
}

private actor ActiveSessionAPI: APIClientProtocol {
    private let session: ReadingSession?
    private let entry: LibraryBook?
    private let book: Book?
    private var paths: [String] = []

    init(session: ReadingSession?, entry: LibraryBook?, book: Book?) {
        self.session = session
        self.entry = entry
        self.book = book
    }

    func requestedPaths() -> [String] { paths }

    func send<T: Decodable & Sendable>(_ endpoint: Endpoint, as type: T.Type) async throws -> T {
        paths.append(endpoint.path)
        let value: any Encodable
        switch endpoint.path {
        case "/api/v1/reading-sessions/active":
            guard let session else { throw APIError.invalidResponse }
            value = session
        case "/api/v1/library":
            value = entry.map { [$0] } ?? []
        default:
            guard let book, endpoint.path == "/api/v1/books/\(book.editionId)" else {
                throw APIError.server(status: 404, message: endpoint.path)
            }
            value = book
        }
        let data = try JSONEncoder().encode(AnyEncodable(value))
        return try JSONDecoder().decode(T.self, from: data)
    }

    func sendVoid(_ endpoint: Endpoint) async throws { paths.append(endpoint.path) }
}

private struct AnyEncodable: Encodable {
    private let encodeValue: (Encoder) throws -> Void

    init(_ value: any Encodable) {
        encodeValue = value.encode
    }

    func encode(to encoder: Encoder) throws {
        try encodeValue(encoder)
    }
}

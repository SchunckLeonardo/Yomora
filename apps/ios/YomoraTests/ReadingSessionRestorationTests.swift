import XCTest
@testable import Yomora

@MainActor
final class ReadingSessionRestorationTests: XCTestCase {
    func testExistingSessionRestoresElapsedTimeAndCurrentPage() {
        let fixture = ReadingSessionFixture()
        let viewModel = ReadingSessionViewModel(
            entry: fixture.entry,
            book: fixture.book,
            api: fixture.api,
            activity: NoopReadingActivityManager(),
            existingSession: fixture.runningSession,
            restoredAt: Date(timeIntervalSince1970: 2_000)
        )

        XCTAssertEqual(viewModel.state, .running)
        XCTAssertEqual(viewModel.endPage, 47)
        XCTAssertEqual(viewModel.timer.elapsed(at: Date(timeIntervalSince1970: 2_100)), 1_000)
    }

    func testProgressPauseAndResumeAreSynchronizedWithTheServer() async {
        let fixture = ReadingSessionFixture()
        let coordinator = ActiveReadingSessionCoordinator(api: fixture.api)
        coordinator.set(session: fixture.runningSession, entry: fixture.entry, book: fixture.book)
        let viewModel = ReadingSessionViewModel(
            entry: fixture.entry,
            book: fixture.book,
            api: fixture.api,
            activity: NoopReadingActivityManager(),
            coordinator: coordinator,
            existingSession: fixture.runningSession,
            restoredAt: Date(timeIntervalSince1970: 2_000)
        )

        viewModel.endPage = 52
        await viewModel.updateProgress()
        await viewModel.pause(at: Date(timeIntervalSince1970: 2_100))
        await viewModel.resume(at: Date(timeIntervalSince1970: 2_200))

        XCTAssertEqual(viewModel.state, .running)
        XCTAssertEqual(coordinator.context?.session.currentPage, 52)
        let requests = await fixture.api.requests()
        XCTAssertEqual(
            requests.map { "\($0.method.rawValue) \($0.path)" },
            [
                "PATCH /api/v1/reading-sessions/\(fixture.runningSession.id)/progress",
                "PATCH /api/v1/reading-sessions/\(fixture.runningSession.id)/pause",
                "PATCH /api/v1/reading-sessions/\(fixture.runningSession.id)/resume"
            ]
        )
    }
}

private struct ReadingSessionFixture {
    let entry: LibraryBook
    let book: Book
    let runningSession: ReadingSession
    let api: SessionSyncAPI

    init() {
        let userId = UUID()
        let entry = LibraryBook(
            id: UUID(), userId: userId, editionId: UUID(), status: .reading, currentPage: 40,
            startedAt: "2026-07-01T10:00:00Z", finishedAt: nil, rating: nil,
            targetFinishDate: nil, createdAt: "2026-07-01T10:00:00Z", updatedAt: "2026-07-01T10:00:00Z"
        )
        let book = Book(
            workId: UUID(), editionId: entry.editionId, title: "Leitura restaurada",
            description: "", authors: ["Autora Yomora"], categories: [], isbn10: nil,
            isbn13: nil, publisher: nil, publicationDate: nil, language: "pt",
            pageCount: 180, coverUrl: nil, externalProvider: nil, externalId: nil
        )
        let session = ReadingSession(
            id: UUID(), userId: userId, userBookId: entry.id, startPage: 40, currentPage: 47,
            endPage: nil, goalPages: nil, startedAt: "1970-01-01T00:16:40Z",
            pausedAt: nil, pausedSeconds: 100, finishedAt: nil, durationSeconds: nil,
            pagesRead: nil, note: nil
        )
        self.entry = entry
        self.book = book
        runningSession = session
        api = SessionSyncAPI(session: session)
    }
}

private actor SessionSyncAPI: APIClientProtocol {
    private var session: ReadingSession
    private var capturedRequests: [Endpoint] = []

    init(session: ReadingSession) {
        self.session = session
    }

    func requests() -> [Endpoint] { capturedRequests }

    func send<T: Decodable & Sendable>(_ endpoint: Endpoint, as type: T.Type) async throws -> T {
        capturedRequests.append(endpoint)
        switch endpoint.path {
        case _ where endpoint.path.hasSuffix("/progress"):
            let body = try JSONDecoder().decode(ProgressBody.self, from: try XCTUnwrap(endpoint.body))
            session = session.updating(currentPage: body.currentPage)
        case _ where endpoint.path.hasSuffix("/pause"):
            session = session.updating(pausedAt: "1970-01-01T00:35:00Z")
        case _ where endpoint.path.hasSuffix("/resume"):
            session = session.updating(pausedAt: .some(nil), pausedSeconds: 200)
        default:
            throw APIError.server(status: 404, message: endpoint.path)
        }
        return try JSONDecoder().decode(T.self, from: session.encoded)
    }

    func sendVoid(_ endpoint: Endpoint) async throws { }
}

private struct ProgressBody: Decodable {
    let currentPage: Int
}

private extension ReadingSession {
    func updating(
        currentPage: Int? = nil,
        pausedAt: String?? = nil,
        pausedSeconds: Int? = nil
    ) -> ReadingSession {
        ReadingSession(
            id: id, userId: userId, userBookId: userBookId, startPage: startPage,
            currentPage: currentPage ?? self.currentPage, endPage: endPage, goalPages: goalPages,
            startedAt: startedAt, pausedAt: pausedAt ?? self.pausedAt,
            pausedSeconds: pausedSeconds ?? self.pausedSeconds, finishedAt: finishedAt,
            durationSeconds: durationSeconds, pagesRead: pagesRead, note: note
        )
    }
}

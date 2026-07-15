import XCTest
@testable import Yomora

final class ReadingActivityManagerTests: XCTestCase {
    func testRunningSnapshotExcludesPreviousPauseIntervals() throws {
        let session = makeSession(pausedAt: nil, pausedSeconds: 100)

        let snapshot = try XCTUnwrap(
            ReadingActivitySnapshot(session: session, bookTitle: "Leitura viva", at: Date(timeIntervalSince1970: 2_000))
        )

        XCTAssertEqual(snapshot.status, .running)
        XCTAssertEqual(snapshot.elapsedSeconds, 900)
        XCTAssertEqual(snapshot.currentPage, 47)
    }

    func testPausedSnapshotDoesNotCountTheOpenPause() throws {
        let session = makeSession(pausedAt: "1970-01-01T00:25:00Z", pausedSeconds: 100)

        let snapshot = try XCTUnwrap(
            ReadingActivitySnapshot(session: session, bookTitle: "Leitura viva", at: Date(timeIntervalSince1970: 2_000))
        )

        XCTAssertEqual(snapshot.status, .paused)
        XCTAssertEqual(snapshot.elapsedSeconds, 400)
    }

    private func makeSession(pausedAt: String?, pausedSeconds: Int) -> ReadingSession {
        ReadingSession(
            id: UUID(), userId: UUID(), userBookId: UUID(), startPage: 40, currentPage: 47,
            endPage: nil, goalPages: nil, startedAt: "1970-01-01T00:16:40Z",
            pausedAt: pausedAt, pausedSeconds: pausedSeconds, finishedAt: nil,
            durationSeconds: nil, pagesRead: nil, note: nil
        )
    }
}

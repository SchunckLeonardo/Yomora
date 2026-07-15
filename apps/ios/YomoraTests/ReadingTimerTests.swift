import XCTest
@testable import Yomora

final class ReadingTimerTests: XCTestCase {
    func testElapsedTimeSurvivesPauseAndBackgroundTime() {
        var timer = ReadingTimer()
        let start = Date(timeIntervalSince1970: 1_000)
        timer.start(at: start)
        timer.pause(at: start.addingTimeInterval(120))

        XCTAssertEqual(timer.elapsed(at: start.addingTimeInterval(1_000)), 120)

        timer.start(at: start.addingTimeInterval(1_000))
        XCTAssertEqual(timer.elapsed(at: start.addingTimeInterval(1_060)), 180)
    }

    func testRestoresRunningSessionExcludingPreviousPauseIntervals() {
        var timer = ReadingTimer()
        let sessionStart = Date(timeIntervalSince1970: 1_000)
        let restoredAt = sessionStart.addingTimeInterval(1_800)

        timer.restore(
            sessionStartedAt: sessionStart,
            pausedAt: nil,
            pausedSeconds: 600,
            at: restoredAt
        )

        XCTAssertTrue(timer.isRunning)
        XCTAssertEqual(timer.elapsed(at: restoredAt), 1_200)
        XCTAssertEqual(timer.elapsed(at: restoredAt.addingTimeInterval(60)), 1_260)
    }

    func testRestoresPausedSessionWithoutCountingTheOpenPause() {
        var timer = ReadingTimer()
        let sessionStart = Date(timeIntervalSince1970: 1_000)
        let pausedAt = sessionStart.addingTimeInterval(900)

        timer.restore(
            sessionStartedAt: sessionStart,
            pausedAt: pausedAt,
            pausedSeconds: 300,
            at: pausedAt.addingTimeInterval(3_000)
        )

        XCTAssertFalse(timer.isRunning)
        XCTAssertEqual(timer.elapsed(at: pausedAt.addingTimeInterval(3_000)), 600)
    }
}

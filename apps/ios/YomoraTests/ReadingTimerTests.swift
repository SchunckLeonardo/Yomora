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
}


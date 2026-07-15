import ActivityKit
import Foundation

enum ReadingActivityStatus: String, Codable, Hashable, Sendable {
    case running
    case paused
}

struct ReadingActivityAttributes: ActivityAttributes {
    struct ContentState: Codable, Hashable {
        let currentPage: Int
        let status: ReadingActivityStatus
        let elapsedSeconds: Int
        let timerStartedAt: Date?
    }

    let sessionId: UUID
    let bookTitle: String
}

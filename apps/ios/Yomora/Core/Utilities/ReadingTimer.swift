import Foundation

struct ReadingTimer: Sendable, Equatable {
    private(set) var startedAt: Date?
    private(set) var accumulated: TimeInterval = 0

    var isRunning: Bool { startedAt != nil }

    mutating func start(at date: Date = .now) {
        guard startedAt == nil else { return }
        startedAt = date
    }

    mutating func pause(at date: Date = .now) {
        guard let startedAt else { return }
        accumulated += max(0, date.timeIntervalSince(startedAt))
        self.startedAt = nil
    }

    mutating func restore(
        sessionStartedAt: Date,
        pausedAt: Date?,
        pausedSeconds: TimeInterval,
        at restoredAt: Date = .now
    ) {
        let effectiveEnd = pausedAt ?? restoredAt
        accumulated = max(0, effectiveEnd.timeIntervalSince(sessionStartedAt) - pausedSeconds)
        startedAt = pausedAt == nil ? restoredAt : nil
    }

    func elapsed(at date: Date = .now) -> TimeInterval {
        accumulated + (startedAt.map { max(0, date.timeIntervalSince($0)) } ?? 0)
    }
}

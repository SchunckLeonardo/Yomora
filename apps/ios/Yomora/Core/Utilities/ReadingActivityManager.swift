import ActivityKit
import Foundation

struct ReadingActivitySnapshot: Sendable, Equatable {
    let sessionId: UUID
    let bookTitle: String
    let currentPage: Int
    let status: ReadingActivityStatus
    let elapsedSeconds: Int

    init?(session: ReadingSession, bookTitle: String, at date: Date = .now) {
        guard let startedAt = Self.serverDate(session.startedAt),
              session.pausedAt == nil || Self.serverDate(session.pausedAt!) != nil else { return nil }
        let pausedAt = session.pausedAt.flatMap(Self.serverDate)
        let effectiveEnd = pausedAt ?? date

        sessionId = session.id
        self.bookTitle = bookTitle
        currentPage = session.currentPage
        status = pausedAt == nil ? .running : .paused
        elapsedSeconds = max(0, Int(effectiveEnd.timeIntervalSince(startedAt)) - session.pausedSeconds)
    }

    private static func serverDate(_ value: String) -> Date? {
        let fractional = ISO8601DateFormatter()
        fractional.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        if let date = fractional.date(from: value) { return date }
        return ISO8601DateFormatter().date(from: value)
    }
}

protocol ReadingActivityManaging: Sendable {
    func start(_ snapshot: ReadingActivitySnapshot) async
    func update(_ snapshot: ReadingActivitySnapshot) async
    func end() async
}

struct NoopReadingActivityManager: ReadingActivityManaging {
    func start(_ snapshot: ReadingActivitySnapshot) async { }
    func update(_ snapshot: ReadingActivitySnapshot) async { }
    func end() async { }
}

struct ActivityKitReadingActivityManager: ReadingActivityManaging {
    func start(_ snapshot: ReadingActivitySnapshot) async {
        guard ActivityAuthorizationInfo().areActivitiesEnabled else { return }
        if let activity = activity(for: snapshot.sessionId) {
            await activity.update(content(for: snapshot))
            return
        }
        for activity in Activity<ReadingActivityAttributes>.activities {
            await activity.end(nil, dismissalPolicy: .immediate)
        }
        let attributes = ReadingActivityAttributes(
            sessionId: snapshot.sessionId,
            bookTitle: snapshot.bookTitle
        )
        _ = try? Activity.request(
            attributes: attributes,
            content: content(for: snapshot),
            pushType: nil
        )
    }

    func update(_ snapshot: ReadingActivitySnapshot) async {
        guard let activity = activity(for: snapshot.sessionId) else {
            await start(snapshot)
            return
        }
        await activity.update(content(for: snapshot))
    }

    func end() async {
        for activity in Activity<ReadingActivityAttributes>.activities {
            await activity.end(nil, dismissalPolicy: .immediate)
        }
    }

    private func activity(for sessionId: UUID) -> Activity<ReadingActivityAttributes>? {
        Activity<ReadingActivityAttributes>.activities.first { $0.attributes.sessionId == sessionId }
    }

    private func content(for snapshot: ReadingActivitySnapshot, at date: Date = .now)
        -> ActivityContent<ReadingActivityAttributes.ContentState> {
        let timerStartedAt = snapshot.status == .running
            ? date.addingTimeInterval(-TimeInterval(snapshot.elapsedSeconds))
            : nil
        return ActivityContent(
            state: ReadingActivityAttributes.ContentState(
                currentPage: snapshot.currentPage,
                status: snapshot.status,
                elapsedSeconds: snapshot.elapsedSeconds,
                timerStartedAt: timerStartedAt
            ),
            staleDate: nil
        )
    }
}

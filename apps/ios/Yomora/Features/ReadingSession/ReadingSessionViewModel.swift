import Foundation
import Observation

@MainActor
@Observable
final class ReadingSessionViewModel {
    enum State: Equatable { case idle, starting, running, paused, finishing, finished, error(String) }
    private(set) var state: State = .idle
    private(set) var timer = ReadingTimer()
    private(set) var session: ReadingSession?
    private(set) var summary: SessionSummary?
    var endPage: Int
    var note = ""

    private let entry: LibraryBook
    private let title: String
    private let api: any APIClientProtocol
    private let activity: any ReadingActivityManaging

    init(entry: LibraryBook, title: String, api: any APIClientProtocol, activity: any ReadingActivityManaging) {
        self.entry = entry; self.title = title; self.api = api; self.activity = activity
        endPage = entry.currentPage
    }

    func start(at date: Date = .now) async {
        struct Body: Encodable { let userBookId: UUID; let startPage: Int; let goalPages: Int? }
        state = .starting
        do {
            var endpoint = Endpoint(path: "/api/v1/reading-sessions", method: .post)
            endpoint.body = try Endpoint.json(Body(userBookId: entry.id, startPage: entry.currentPage, goalPages: nil))
            session = try await api.send(endpoint, as: ReadingSession.self)
            timer.start(at: date)
            await activity.start(bookTitle: title, startedAt: date)
            state = .running
        } catch { state = .error(error.localizedDescription) }
    }

    func pause(at date: Date = .now) {
        timer.pause(at: date); state = .paused
    }

    func resume(at date: Date = .now) {
        timer.start(at: date); state = .running
    }

    func finish() async {
        guard let session else { return }
        struct Body: Encodable { let endPage: Int; let note: String? }
        state = .finishing
        do {
            var endpoint = Endpoint(path: "/api/v1/reading-sessions/\(session.id)/finish", method: .patch)
            endpoint.body = try Endpoint.json(Body(endPage: endPage, note: note.isEmpty ? nil : note))
            summary = try await api.send(endpoint, as: SessionSummary.self)
            await activity.end()
            state = .finished
        } catch { state = .error(error.localizedDescription) }
    }
}


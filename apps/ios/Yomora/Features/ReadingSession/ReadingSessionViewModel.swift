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
    private(set) var syncError: String?
    var endPage: Int
    var note = ""

    private let entry: LibraryBook
    private let title: String
    private let api: any APIClientProtocol
    private let activity: any ReadingActivityManaging
    private let coordinator: ActiveReadingSessionCoordinator?
    private var book: Book?
    @ObservationIgnored private var progressTask: Task<Void, Never>?
    @ObservationIgnored private var hasPendingProgress = false

    init(
        entry: LibraryBook,
        book: Book? = nil,
        title: String? = nil,
        api: any APIClientProtocol,
        activity: any ReadingActivityManaging,
        coordinator: ActiveReadingSessionCoordinator? = nil,
        existingSession: ReadingSession? = nil,
        restoredAt: Date = .now
    ) {
        self.entry = entry
        self.book = book
        self.title = title ?? book?.title ?? "Sua leitura"
        self.api = api
        self.activity = activity
        self.coordinator = coordinator
        session = existingSession
        endPage = existingSession?.currentPage ?? entry.currentPage

        if let existingSession {
            restoreTimer(from: existingSession, at: restoredAt)
        }
    }

    func start(at date: Date = .now) async {
        struct Body: Encodable { let userBookId: UUID; let startPage: Int; let goalPages: Int? }
        state = .starting
        do {
            var endpoint = Endpoint(path: "/api/v1/reading-sessions", method: .post)
            endpoint.body = try Endpoint.json(Body(userBookId: entry.id, startPage: entry.currentPage, goalPages: nil))
            let startedSession = try await api.send(endpoint, as: ReadingSession.self)
            session = startedSession
            endPage = startedSession.currentPage
            timer.start(at: date)
            await activity.start(bookTitle: title, startedAt: date)
            await publishActiveContext(startedSession)
            syncError = nil
            state = .running
        } catch { state = .error(error.localizedDescription) }
    }

    func pause(at date: Date = .now) async {
        guard state == .running else { return }
        if hasPendingProgress {
            progressTask?.cancel()
            hasPendingProgress = false
            await updateProgress()
        }
        guard let session else { return }
        state = .starting
        do {
            let paused = try await api.send(
                Endpoint(path: "/api/v1/reading-sessions/\(session.id)/pause", method: .patch),
                as: ReadingSession.self
            )
            apply(paused, restoredAt: date)
            syncError = nil
        } catch {
            state = .running
            syncError = error.localizedDescription
        }
    }

    func resume(at date: Date = .now) async {
        guard let session, state == .paused else { return }
        state = .starting
        do {
            let resumed = try await api.send(
                Endpoint(path: "/api/v1/reading-sessions/\(session.id)/resume", method: .patch),
                as: ReadingSession.self
            )
            apply(resumed, restoredAt: date)
            syncError = nil
        } catch {
            state = .paused
            syncError = error.localizedDescription
        }
    }

    func scheduleProgressUpdate() {
        progressTask?.cancel()
        hasPendingProgress = true
        progressTask = Task { [weak self] in
            try? await Task.sleep(for: .milliseconds(300))
            guard !Task.isCancelled else { return }
            self?.hasPendingProgress = false
            await self?.updateProgress()
        }
    }

    func updateProgress() async {
        guard let session, state != .finishing, state != .finished else { return }
        struct Body: Encodable { let currentPage: Int }
        do {
            var endpoint = Endpoint(path: "/api/v1/reading-sessions/\(session.id)/progress", method: .patch)
            endpoint.body = try Endpoint.json(Body(currentPage: endPage))
            let updated = try await api.send(endpoint, as: ReadingSession.self)
            self.session = updated
            coordinator?.update(updated)
            syncError = nil
        } catch {
            syncError = "Não foi possível sincronizar a página: \(error.localizedDescription)"
        }
    }

    func finish() async {
        guard let session else { return }
        progressTask?.cancel()
        hasPendingProgress = false
        struct Body: Encodable { let endPage: Int; let note: String? }
        state = .finishing
        do {
            var endpoint = Endpoint(path: "/api/v1/reading-sessions/\(session.id)/finish", method: .patch)
            let trimmedNote = note.trimmingCharacters(in: .whitespacesAndNewlines)
            endpoint.body = try Endpoint.json(Body(endPage: endPage, note: trimmedNote.isEmpty ? nil : trimmedNote))
            summary = try await api.send(endpoint, as: SessionSummary.self)
            await activity.end()
            coordinator?.clear()
            syncError = nil
            state = .finished
            NotificationCenter.default.post(name: .libraryDidChange, object: nil)
        } catch { state = .error(error.localizedDescription) }
    }

    private func publishActiveContext(_ session: ReadingSession) async {
        if book == nil {
            book = try? await api.send(
                Endpoint(path: "/api/v1/books/\(entry.editionId)", authenticated: false),
                as: Book.self
            )
        }
        if let book {
            coordinator?.set(session: session, entry: entry, book: book)
        }
    }

    private func apply(_ session: ReadingSession, restoredAt: Date) {
        self.session = session
        endPage = session.currentPage
        coordinator?.update(session)
        restoreTimer(from: session, at: restoredAt)
    }

    private func restoreTimer(from session: ReadingSession, at restoredAt: Date) {
        guard let startedAt = Self.serverDate(session.startedAt),
              session.pausedAt == nil || Self.serverDate(session.pausedAt!) != nil else {
            state = .error("A data da sessão retornada pelo servidor é inválida.")
            return
        }
        timer.restore(
            sessionStartedAt: startedAt,
            pausedAt: session.pausedAt.flatMap(Self.serverDate),
            pausedSeconds: TimeInterval(session.pausedSeconds),
            at: restoredAt
        )
        state = session.pausedAt == nil ? .running : .paused
    }

    private static func serverDate(_ value: String) -> Date? {
        let fractional = ISO8601DateFormatter()
        fractional.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        if let date = fractional.date(from: value) { return date }
        return ISO8601DateFormatter().date(from: value)
    }
}

import Foundation
import Observation

@MainActor
@Observable
final class TodayViewModel {
    enum State: Equatable { case idle, loading, loaded, error(String) }
    private(set) var state: State = .idle
    private(set) var statistics: StatisticsSummary?
    private(set) var library: [LibraryBook] = []
    private(set) var featuredBook: Book?
    private(set) var goal = ReadingGoal(dailyMinutes: 20, weeklyDays: 5, dailyPages: nil)
    private let api: any APIClientProtocol

    init(api: any APIClientProtocol) { self.api = api }

    func load(defaultMinutes: Int, weeklyDays: Int) async {
        state = .loading
        do {
            async let stats = api.send(Endpoint(path: "/api/v1/statistics/summary"), as: StatisticsSummary.self)
            async let books = api.send(Endpoint(path: "/api/v1/library"), as: [LibraryBook].self)
            statistics = try await stats
            library = try await books
            goal = await loadGoal(defaultMinutes: defaultMinutes, weeklyDays: weeklyDays)
            if let editionId = library.first(where: { $0.status == .reading })?.editionId {
                featuredBook = try? await api.send(Endpoint(path: "/api/v1/books/\(editionId)"), as: Book.self)
            }
            state = .loaded
        } catch { state = .error(error.localizedDescription) }
    }

    private func loadGoal(defaultMinutes: Int, weeklyDays: Int) async -> ReadingGoal {
        if let existing = try? await api.send(Endpoint(path: "/api/v1/reading-goals"), as: ReadingGoal.self) {
            return existing
        }
        struct Body: Encodable { let dailyMinutes: Int; let weeklyDays: Int; let dailyPages: Int? }
        var endpoint = Endpoint(path: "/api/v1/reading-goals", method: .put)
        endpoint.body = try? Endpoint.json(Body(dailyMinutes: defaultMinutes, weeklyDays: weeklyDays, dailyPages: nil))
        return (try? await api.send(endpoint, as: ReadingGoal.self)) ?? goal
    }
}


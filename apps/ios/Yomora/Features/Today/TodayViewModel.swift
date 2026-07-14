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
    private var loadGeneration = 0

    init(api: any APIClientProtocol) { self.api = api }

    func load(defaultMinutes: Int, weeklyDays: Int) async {
        loadGeneration += 1
        let generation = loadGeneration
        let previousState = state
        if previousState != .loaded { state = .loading }
        do {
            async let stats = api.send(Endpoint(path: "/api/v1/statistics/summary"), as: StatisticsSummary.self)
            async let books = api.send(Endpoint(path: "/api/v1/library"), as: [LibraryBook].self)
            let newStatistics = try await stats
            let newLibrary = try await books
            let newGoal = await loadGoal(defaultMinutes: defaultMinutes, weeklyDays: weeklyDays)
            var newFeaturedBook: Book?
            if let editionId = newLibrary.first(where: { $0.status == .reading })?.editionId {
                newFeaturedBook = try? await api.send(Endpoint(path: "/api/v1/books/\(editionId)"), as: Book.self)
            }
            try Task.checkCancellation()
            guard generation == loadGeneration else { return }
            statistics = newStatistics
            library = newLibrary
            goal = newGoal
            featuredBook = newFeaturedBook
            state = .loaded
        } catch {
            guard generation == loadGeneration else { return }
            state = Self.isCancellation(error) ? previousState : .error(error.localizedDescription)
        }
    }

    private static func isCancellation(_ error: Error) -> Bool {
        error is CancellationError || (error as? URLError)?.code == .cancelled
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

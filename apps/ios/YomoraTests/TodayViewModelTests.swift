import XCTest
@testable import Yomora

@MainActor
final class TodayViewModelTests: XCTestCase {
    func testCancelledRefreshKeepsLoadedContentVisible() async {
        let api = TodayAPIClient()
        let viewModel = TodayViewModel(api: api)

        await viewModel.load(defaultMinutes: 20, weeklyDays: 5)
        XCTAssertEqual(viewModel.state, .loaded)

        await api.cancelNextStatisticsRequest()
        await viewModel.load(defaultMinutes: 20, weeklyDays: 5)

        XCTAssertEqual(viewModel.state, .loaded)
    }

    func testOlderCancelledLoadCannotOverwriteNewerSuccess() async {
        let api = TodayAPIClient(delaysFirstStatisticsCancellation: true)
        let viewModel = TodayViewModel(api: api)

        let olderLoad = Task { await viewModel.load(defaultMinutes: 20, weeklyDays: 5) }
        try? await Task.sleep(for: .milliseconds(20))
        await viewModel.load(defaultMinutes: 20, weeklyDays: 5)
        await olderLoad.value

        XCTAssertEqual(viewModel.state, .loaded)
    }
}

private actor TodayAPIClient: APIClientProtocol {
    private var shouldCancelStatistics = false
    private var statisticsRequestCount = 0
    private let delaysFirstStatisticsCancellation: Bool

    init(delaysFirstStatisticsCancellation: Bool = false) {
        self.delaysFirstStatisticsCancellation = delaysFirstStatisticsCancellation
    }

    func cancelNextStatisticsRequest() {
        shouldCancelStatistics = true
    }

    func send<T: Decodable & Sendable>(_ endpoint: Endpoint, as type: T.Type) async throws -> T {
        if endpoint.path == "/api/v1/statistics/summary" {
            statisticsRequestCount += 1
            if delaysFirstStatisticsCancellation, statisticsRequestCount == 1 {
                try? await Task.sleep(for: .milliseconds(150))
                throw URLError(.cancelled)
            }
            if shouldCancelStatistics {
                shouldCancelStatistics = false
                throw URLError(.cancelled)
            }
        }

        let data: Data
        switch endpoint.path {
        case "/api/v1/statistics/summary":
            data = StatisticsSummary(
                minutesToday: 1,
                minutesThisWeek: 1,
                pagesThisWeek: 1,
                daysReadLast30: 1,
                currentStreak: 1,
                bestStreak: 1,
                finishedBooksThisYear: 0,
                topGenres: [:],
                totalSessions: 1,
                pacePercent: 5
            ).encoded
        case "/api/v1/library":
            data = [LibraryBook]().encoded
        case "/api/v1/reading-goals":
            data = ReadingGoal(dailyMinutes: 20, weeklyDays: 5, dailyPages: nil).encoded
        default:
            throw APIError.server(status: 404, message: endpoint.path)
        }
        return try JSONDecoder().decode(T.self, from: data)
    }

    func sendVoid(_ endpoint: Endpoint) async throws { }
}

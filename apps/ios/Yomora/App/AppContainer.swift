import Foundation
import SwiftData

@MainActor
final class AppContainer {
    let api: any APIClientProtocol
    let tokenStore: any TokenStoring
    let bookCache: BookCache
    let modelContainer: ModelContainer
    let readingActivity: any ReadingActivityManaging
    let activeReadingSession: ActiveReadingSessionCoordinator
    let navigation: AppNavigationCoordinator
    let readingReminders: any ReadingReminderManaging

    init(api: any APIClientProtocol, tokenStore: any TokenStoring, modelContainer: ModelContainer,
         readingActivity: any ReadingActivityManaging = NoopReadingActivityManager(),
         readingReminders: any ReadingReminderManaging = NoopReadingReminderManager()) {
        self.api = api
        self.tokenStore = tokenStore
        self.modelContainer = modelContainer
        self.bookCache = BookCache(container: modelContainer)
        self.readingActivity = readingActivity
        self.activeReadingSession = ActiveReadingSessionCoordinator(api: api, activity: readingActivity)
        self.navigation = AppNavigationCoordinator()
        self.readingReminders = readingReminders
    }

    static func live() -> AppContainer {
        #if DEBUG
        if ProcessInfo.processInfo.arguments.contains("--ui-testing") {
            let store = InMemoryTokenStore()
            let configuration = ModelConfiguration(isStoredInMemoryOnly: true)
            let container = try! ModelContainer(for: CachedBook.self, configurations: configuration)
            return AppContainer(api: UITestAPIClient(), tokenStore: store, modelContainer: container)
        }
        #endif
        let store = KeychainTokenStore()
        let configured = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String
        let baseURL = URL(string: configured ?? "http://localhost:8080")!
        let container = try! ModelContainer(for: CachedBook.self)
        return AppContainer(
            api: APIClient(baseURL: baseURL, tokenStore: store),
            tokenStore: store,
            modelContainer: container,
            readingActivity: ActivityKitReadingActivityManager(),
            readingReminders: LocalReadingReminderManager()
        )
    }
}

import SwiftUI
import UIKit
import XCTest
@testable import Yomora

final class TokenAndNavigationTests: XCTestCase {
    func testInMemoryTokenStoreRoundTripAndClear() async throws {
        let store = InMemoryTokenStore()
        let pair = TokenPair(accessToken: "a", refreshToken: "r", tokenType: "Bearer", expiresIn: 60)
        await store.save(pair)
        let stored = await store.load()
        XCTAssertEqual(stored, pair)
        await store.clear()
        let cleared = await store.load()
        XCTAssertNil(cleared)
    }

    func testTypedRoutesAreHashable() {
        let id = UUID()
        XCTAssertEqual(Set([AppRoute.profile(id), AppRoute.profile(id)]).count, 1)
    }
}

@MainActor
final class ActivityInboxTests: XCTestCase {
    func testFormatsActivityDatesForPeopleInsteadOfShowingRawISO8601() throws {
        let now = try XCTUnwrap(
            ISO8601DateFormatter().date(from: "2026-07-17T18:22:04Z")
        )

        XCTAssertEqual(
            ActivityDate.display(
                "2026-07-17T18:20:03.433567Z",
                relativeTo: now,
                timeZone: try XCTUnwrap(TimeZone(secondsFromGMT: 0))
            ),
            "há 2 min"
        )
    }

    func testMarkingActivityAsReadUpdatesUnreadBadgeCount() async {
        let api = ActivityInboxAPI()
        let model = ActivityInboxViewModel(api: api)

        await model.load()
        XCTAssertEqual(model.unreadCount, 2)

        await model.markRead(model.activities[0])
        XCTAssertEqual(model.unreadCount, 1)
    }
}

final class YomoraDesignTests: XCTestCase {
    func testDarkPaletteKeepsReadingTextAtAAContrast() {
        let canvas = resolvedRGB(YomoraColor.canvas, style: .dark)
        let surface = resolvedRGB(YomoraColor.surface, style: .dark)

        XCTAssertGreaterThanOrEqual(contrast(canvas, resolvedRGB(YomoraColor.textPrimary, style: .dark)), 4.5)
        XCTAssertGreaterThanOrEqual(contrast(canvas, resolvedRGB(YomoraColor.textSecondary, style: .dark)), 4.5)
        XCTAssertGreaterThanOrEqual(contrast(surface, resolvedRGB(YomoraColor.textPrimary, style: .dark)), 4.5)
    }

    func testDarkCommunityAccentIsReadableOnElevatedSurface() {
        let surface = resolvedRGB(YomoraColor.surfaceElevated, style: .dark)
        let accent = resolvedRGB(YomoraColor.sereneTeal, style: .dark)

        XCTAssertGreaterThanOrEqual(contrast(surface, accent), 4.5)
    }

    func testFocusInputContentIsReadableOnWhiteFieldInDarkMode() {
        let background = resolvedRGB(YomoraColor.focusInputBackground, style: .dark)
        let placeholder = resolvedRGB(YomoraColor.focusInputPlaceholder, style: .dark)
        let text = resolvedRGB(YomoraColor.focusInputText, style: .dark)

        XCTAssertGreaterThanOrEqual(contrast(background, placeholder), 4.5)
        XCTAssertGreaterThanOrEqual(contrast(background, text), 4.5)
    }

    private func resolvedRGB(_ color: Color, style: UIUserInterfaceStyle) -> RGB {
        let trait = UITraitCollection(userInterfaceStyle: style)
        let resolved = UIColor(color).resolvedColor(with: trait)
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        XCTAssertTrue(resolved.getRed(&red, green: &green, blue: &blue, alpha: &alpha))
        return RGB(red: Double(red), green: Double(green), blue: Double(blue))
    }

    private func contrast(_ lhs: RGB, _ rhs: RGB) -> Double {
        let lighter = max(lhs.luminance, rhs.luminance)
        let darker = min(lhs.luminance, rhs.luminance)
        return (lighter + 0.05) / (darker + 0.05)
    }

    private struct RGB {
        let red: Double
        let green: Double
        let blue: Double

        var luminance: Double {
            0.2126 * linear(red) + 0.7152 * linear(green) + 0.0722 * linear(blue)
        }

        private func linear(_ component: Double) -> Double {
            component <= 0.04045 ? component / 12.92 : pow((component + 0.055) / 1.055, 2.4)
        }
    }
}

final class KeyboardLayoutTests: XCTestCase {
    func testBottomInsetReservesOnlyKeyboardAreaAboveTheHomeIndicator() {
        XCTAssertEqual(
            KeyboardLayout.bottomInset(screenHeight: 852, keyboardMinY: 518, bottomSafeArea: 34),
            300
        )
    }

    func testBottomInsetIsZeroWhenKeyboardIsHidden() {
        XCTAssertEqual(
            KeyboardLayout.bottomInset(screenHeight: 852, keyboardMinY: 852, bottomSafeArea: 34),
            0
        )
    }
}

final class AppConfigurationTests: XCTestCase {
    func testApplicationBundleContainsAValidAPIBaseURL() throws {
        let configured = try XCTUnwrap(Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String)
        let url = try XCTUnwrap(URL(string: configured))

        XCTAssertNotNil(url.scheme)
        XCTAssertNotNil(url.host)
    }
}

@MainActor
final class FeedViewModelTests: XCTestCase {
    func testLikeActionTogglesVisualStateAndUsesMatchingEndpointMethods() async {
        let post = makePost(likeCount: 0)
        let api = FeedEngagementAPI(post: post)
        let viewModel = FeedViewModel(api: api)
        await viewModel.load()

        XCTAssertFalse(viewModel.isLiked(post))

        await viewModel.toggleLike(post)

        XCTAssertTrue(viewModel.isLiked(post))
        XCTAssertEqual(viewModel.posts.first?.likeCount, 1)

        await viewModel.toggleLike(viewModel.posts[0])

        XCTAssertFalse(viewModel.isLiked(post))
        XCTAssertEqual(viewModel.posts.first?.likeCount, 0)
        let methods = await api.methods()
        XCTAssertEqual(methods, [.get, .post, .delete])
    }

    func testFailedOptimisticLikeRollsBackCountAndSelection() async {
        let post = makePost(likeCount: 2)
        let api = FailingFeedEngagementAPI(post: post)
        let viewModel = FeedViewModel(api: api)
        await viewModel.load()

        await viewModel.toggleLike(post)

        XCTAssertFalse(viewModel.isLiked(post))
        XCTAssertEqual(viewModel.posts.first?.likeCount, 2)
        XCTAssertNotNil(viewModel.interactionError)
    }

    private func makePost(likeCount: Int) -> Post {
        Post(
            id: UUID(),
            authorId: UUID(),
            text: "Uma leitura que permanece depois da última página.",
            editionId: nil,
            type: .recommendation,
            spoiler: false,
            spoilerPage: nil,
            visibility: .publicPost,
            createdAt: "2026-07-14T10:00:00Z",
            updatedAt: "2026-07-14T10:00:00Z",
            likeCount: likeCount,
            commentCount: 2
        )
    }
}

private actor FailingFeedEngagementAPI: APIClientProtocol {
    let post: Post

    init(post: Post) { self.post = post }

    func send<T: Decodable & Sendable>(_ endpoint: Endpoint, as type: T.Type) async throws -> T {
        if endpoint.path.hasSuffix("/likes") {
            throw APIError.server(status: 503, message: "Tente novamente")
        }
        return try JSONDecoder().decode(T.self, from: [post].encoded)
    }

    func sendVoid(_ endpoint: Endpoint) async throws { }
}

private actor ActivityInboxAPI: APIClientProtocol {
    private let recipientId = UUID()
    private let actorId = UUID()
    private var activities: [CommunityActivity]

    init() {
        activities = [
            CommunityActivity(
                id: UUID(),
                recipientId: recipientId,
                actorId: actorId,
                type: .userFollowed,
                postId: nil,
                read: false,
                createdAt: "2026-07-17T18:20:03Z"
            ),
            CommunityActivity(
                id: UUID(),
                recipientId: recipientId,
                actorId: actorId,
                type: .postLiked,
                postId: UUID(),
                read: false,
                createdAt: "2026-07-17T18:21:03Z"
            )
        ]
    }

    func send<T: Decodable & Sendable>(_ endpoint: Endpoint, as type: T.Type) async throws -> T {
        let data: Data
        if endpoint.method == .get, endpoint.path == "/api/v1/community/activities" {
            data = activities.encoded
        } else if endpoint.method == .patch,
                  let id = activities.first(where: { endpoint.path.hasSuffix("/\($0.id)/read") })?.id,
                  let index = activities.firstIndex(where: { $0.id == id }) {
            let current = activities[index]
            activities[index] = CommunityActivity(
                id: current.id,
                recipientId: current.recipientId,
                actorId: current.actorId,
                type: current.type,
                postId: current.postId,
                read: true,
                createdAt: current.createdAt
            )
            data = activities[index].encoded
        } else {
            throw APIError.server(status: 404, message: endpoint.path)
        }
        return try JSONDecoder().decode(T.self, from: data)
    }

    func sendVoid(_ endpoint: Endpoint) async throws { }
}

private actor FeedEngagementAPI: APIClientProtocol {
    private var post: Post
    private var endpoints: [Endpoint] = []

    init(post: Post) { self.post = post }

    func send<T: Decodable & Sendable>(_ endpoint: Endpoint, as type: T.Type) async throws -> T {
        endpoints.append(endpoint)
        let data: Data
        if endpoint.path.hasSuffix("/likes") {
            let nextCount = endpoint.method == .post ? post.likeCount + 1 : max(0, post.likeCount - 1)
            post = Post(
                id: post.id,
                authorId: post.authorId,
                text: post.text,
                editionId: post.editionId,
                type: post.type,
                spoiler: post.spoiler,
                spoilerPage: post.spoilerPage,
                visibility: post.visibility,
                createdAt: post.createdAt,
                updatedAt: post.updatedAt,
                likeCount: nextCount,
                commentCount: post.commentCount
            )
            data = post.encoded
        } else {
            data = [post].encoded
        }
        return try JSONDecoder().decode(T.self, from: data)
    }

    func sendVoid(_ endpoint: Endpoint) async throws { endpoints.append(endpoint) }

    func methods() -> [Endpoint.Method] { endpoints.map(\.method) }
}

final class BookCoverImageCacheTests: XCTestCase {
    func testCoverDataPersistsAcrossCacheInstances() async throws {
        let directory = FileManager.default.temporaryDirectory
            .appendingPathComponent("yomora-cover-cache-\(UUID().uuidString)", isDirectory: true)
        defer { try? FileManager.default.removeItem(at: directory) }
        let source = CoverDataSource()
        let url = try XCTUnwrap(URL(string: "https://example.test/covers/book.jpg"))

        let firstCache = BookCoverImageCache(directory: directory) { url in
            try await source.load(url)
        }
        let firstData = try await firstCache.data(for: url)
        let secondCache = BookCoverImageCache(directory: directory) { url in
            try await source.load(url)
        }
        let secondData = try await secondCache.data(for: url)

        XCTAssertEqual(firstData, secondData)
        let requestCount = await source.requestCount
        XCTAssertEqual(requestCount, 1)
    }
}

@MainActor
final class QuickReadViewModelTests: XCTestCase {
    func testLoadsEveryReadingBookAndAllowsSelectingOne() async {
        let firstEntry = makeLibraryBook(status: .reading)
        let secondEntry = makeLibraryBook(status: .reading)
        let firstBook = makeBook(editionId: firstEntry.editionId, title: "Primeiro livro")
        let secondBook = makeBook(editionId: secondEntry.editionId, title: "Segundo livro")
        let api = ReadingListAPI(entries: [firstEntry, secondEntry], books: [firstBook, secondBook])
        let viewModel = QuickReadViewModel(api: api)

        await viewModel.load()

        XCTAssertEqual(viewModel.items.map(\.book.title), ["Primeiro livro", "Segundo livro"])
        XCTAssertEqual(viewModel.selectedItem?.entry.id, firstEntry.id)

        viewModel.select(secondEntry.id)

        XCTAssertEqual(viewModel.selectedItem?.entry.id, secondEntry.id)
    }

    func testReloadRemovesBooksThatAreNoLongerReading() async {
        let pausedEntry = makeLibraryBook(status: .reading)
        let activeEntry = makeLibraryBook(status: .reading)
        let pausedBook = makeBook(editionId: pausedEntry.editionId, title: "Leitura pausada")
        let activeBook = makeBook(editionId: activeEntry.editionId, title: "Leitura ativa")
        let api = ReadingListAPI(entries: [pausedEntry, activeEntry], books: [pausedBook, activeBook])
        let viewModel = QuickReadViewModel(api: api)
        await viewModel.load()
        viewModel.select(pausedEntry.id)

        await api.replaceEntries([pausedEntry.updatingStatus(.paused), activeEntry])
        await viewModel.load()

        XCTAssertEqual(viewModel.items.map(\.entry.id), [activeEntry.id])
        XCTAssertEqual(viewModel.selectedItem?.entry.id, activeEntry.id)
    }
}

@MainActor
final class TodayViewModelReadingStatusTests: XCTestCase {
    func testPausedAndAbandonedBooksAreNotFeaturedToday() async {
        let paused = makeLibraryBook(status: .paused)
        let abandoned = makeLibraryBook(status: .abandoned)
        let api = TodayLibraryAPI(entries: [paused, abandoned])
        let viewModel = TodayViewModel(api: api)

        await viewModel.load(defaultMinutes: 20, weeklyDays: 5)

        XCTAssertEqual(viewModel.state, .loaded)
        XCTAssertNil(viewModel.featuredBook)
        XCTAssertFalse(viewModel.library.contains(where: { $0.status == .reading }))
    }
}

@MainActor
final class ReadingSessionNoteTests: XCTestCase {
    func testFinishSendsTrimmedNoteAndNotifiesLibraryRefresh() async throws {
        let entry = makeLibraryBook(status: .reading)
        let api = ReadingSessionAPI(entry: entry)
        let viewModel = ReadingSessionViewModel(
            entry: entry,
            title: "Livro em foco",
            api: api,
            activity: NoopReadingActivityManager()
        )
        await viewModel.start(at: Date(timeIntervalSince1970: 1_000))
        viewModel.endPage = entry.currentPage + 5
        viewModel.note = "  Uma ideia importante.  "
        let notification = expectation(forNotification: .libraryDidChange, object: nil)

        await viewModel.finish()

        await fulfillment(of: [notification], timeout: 1)
        XCTAssertEqual(viewModel.state, .finished)
        let capturedBody = await api.capturedFinishBody()
        let bodyData = try XCTUnwrap(capturedBody)
        let body = try JSONDecoder().decode(FinishReadingBody.self, from: bodyData)
        XCTAssertEqual(body.note, "Uma ideia importante.")
        XCTAssertEqual(body.endPage, entry.currentPage + 5)
    }
}

private struct FinishReadingBody: Decodable {
    let endPage: Int
    let note: String?
}

private actor CoverDataSource {
    private(set) var requestCount = 0

    func load(_ url: URL) throws -> Data {
        requestCount += 1
        return Data("cover:\(url.absoluteString)".utf8)
    }
}

private actor ReadingListAPI: APIClientProtocol {
    private var entries: [LibraryBook]
    private let books: [UUID: Book]

    init(entries: [LibraryBook], books: [Book]) {
        self.entries = entries
        self.books = Dictionary(uniqueKeysWithValues: books.map { ($0.editionId, $0) })
    }

    func replaceEntries(_ entries: [LibraryBook]) {
        self.entries = entries
    }

    func send<T: Decodable & Sendable>(_ endpoint: Endpoint, as type: T.Type) async throws -> T {
        let data: Data
        if endpoint.path == "/api/v1/library" {
            data = entries.encoded
        } else if let id = UUID(uuidString: endpoint.path.replacingOccurrences(of: "/api/v1/books/", with: "")),
                  let book = books[id] {
            data = book.encoded
        } else {
            throw APIError.server(status: 404, message: endpoint.path)
        }
        return try JSONDecoder().decode(T.self, from: data)
    }

    func sendVoid(_ endpoint: Endpoint) async throws { }
}

private actor ReadingSessionAPI: APIClientProtocol {
    private let entry: LibraryBook
    private let sessionId = UUID()
    private(set) var finishBody: Data?

    init(entry: LibraryBook) {
        self.entry = entry
    }

    func capturedFinishBody() -> Data? {
        finishBody
    }

    func send<T: Decodable & Sendable>(_ endpoint: Endpoint, as type: T.Type) async throws -> T {
        let data: Data
        switch (endpoint.method, endpoint.path) {
        case (.post, "/api/v1/reading-sessions"):
            data = ReadingSession(
                id: sessionId,
                userId: entry.userId,
                userBookId: entry.id,
                startPage: entry.currentPage,
                currentPage: entry.currentPage,
                endPage: nil,
                goalPages: nil,
                startedAt: "2026-07-15T12:00:00Z",
                pausedAt: nil,
                pausedSeconds: 0,
                finishedAt: nil,
                durationSeconds: nil,
                pagesRead: nil,
                note: nil
            ).encoded
        case (.patch, "/api/v1/reading-sessions/\(sessionId)/finish"):
            finishBody = endpoint.body
            data = SessionSummary(
                sessionId: sessionId,
                durationMinutes: 10,
                pagesRead: 5,
                progressPercent: 20,
                averagePagesPerHour: 30,
                estimatedSessionsRemaining: 10,
                estimatedFinishDate: nil,
                currentStreak: 2
            ).encoded
        default:
            throw APIError.server(status: 404, message: endpoint.path)
        }
        return try JSONDecoder().decode(T.self, from: data)
    }

    func sendVoid(_ endpoint: Endpoint) async throws { }
}

private actor TodayLibraryAPI: APIClientProtocol {
    private let entries: [LibraryBook]

    init(entries: [LibraryBook]) {
        self.entries = entries
    }

    func send<T: Decodable & Sendable>(_ endpoint: Endpoint, as type: T.Type) async throws -> T {
        let data: Data
        switch endpoint.path {
        case "/api/v1/statistics/summary":
            data = StatisticsSummary(
                minutesToday: 0,
                minutesThisWeek: 0,
                pagesThisWeek: 0,
                daysReadLast30: 0,
                currentStreak: 0,
                bestStreak: 0,
                finishedBooksThisYear: 0,
                topGenres: [:],
                totalSessions: 0,
                pacePercent: 0
            ).encoded
        case "/api/v1/library":
            data = entries.encoded
        case "/api/v1/reading-goals":
            data = ReadingGoal(dailyMinutes: 20, weeklyDays: 5, dailyPages: nil).encoded
        default:
            throw APIError.server(status: 404, message: endpoint.path)
        }
        return try JSONDecoder().decode(T.self, from: data)
    }

    func sendVoid(_ endpoint: Endpoint) async throws { }
}

private func makeLibraryBook(status: ReadingStatus) -> LibraryBook {
    LibraryBook(
        id: UUID(),
        userId: UUID(),
        editionId: UUID(),
        status: status,
        currentPage: 12,
        startedAt: "2026-07-15T12:00:00Z",
        finishedAt: nil,
        rating: nil,
        targetFinishDate: nil,
        createdAt: "2026-07-15T12:00:00Z",
        updatedAt: "2026-07-15T12:00:00Z"
    )
}

private func makeBook(editionId: UUID, title: String) -> Book {
    Book(
        workId: UUID(),
        editionId: editionId,
        title: title,
        description: "",
        authors: ["Autora"],
        categories: [],
        isbn10: nil,
        isbn13: nil,
        publisher: nil,
        publicationDate: nil,
        language: "por",
        pageCount: 200,
        coverUrl: nil,
        externalProvider: "test",
        externalId: editionId.uuidString
    )
}

private extension LibraryBook {
    func updatingStatus(_ status: ReadingStatus) -> LibraryBook {
        LibraryBook(
            id: id,
            userId: userId,
            editionId: editionId,
            status: status,
            currentPage: currentPage,
            startedAt: startedAt,
            finishedAt: finishedAt,
            rating: rating,
            targetFinishDate: targetFinishDate,
            createdAt: createdAt,
            updatedAt: updatedAt
        )
    }
}

#if DEBUG
import Foundation

actor UITestAPIClient: APIClientProtocol {
    private let userId = UUID(uuidString: "10000000-0000-0000-0000-000000000001")!
    private let workId = UUID(uuidString: "20000000-0000-0000-0000-000000000001")!
    private let editionId = UUID(uuidString: "30000000-0000-0000-0000-000000000001")!
    private let libraryId = UUID(uuidString: "40000000-0000-0000-0000-000000000001")!
    private let sessionId = UUID(uuidString: "60000000-0000-0000-0000-000000000001")!
    private let postId = UUID(uuidString: "70000000-0000-0000-0000-000000000001")!
    private let commentId = UUID(uuidString: "80000000-0000-0000-0000-000000000001")!
    private var libraryAdded = false

    func send<T: Decodable & Sendable>(_ endpoint: Endpoint, as type: T.Type) async throws -> T {
        let value: any Encodable
        switch (endpoint.method, endpoint.path) {
        case (.post, "/api/v1/auth/login"), (.post, "/api/v1/auth/register"):
            value = TokenPair(accessToken: "ui-access", refreshToken: "ui-refresh", tokenType: "Bearer", expiresIn: 900)
        case (_, "/api/v1/users/me"):
            value = UserProfile(id: userId, name: "Marina Leitora", username: "marina", email: "marina@yomora.local",
                                bio: "Lendo um pouco todos os dias.", avatarUrl: nil, publicProfile: true,
                                followers: 1, following: 1, finishedBooks: 3, totalReadingMinutes: 480,
                                currentStreak: 7, createdAt: "2026-07-01T12:00:00Z")
        case (.get, "/api/v1/books/search"), (.get, "/api/v1/books/\(editionId)"):
            value = endpoint.path.hasSuffix("search") ? [book] : book
        case (.post, "/api/v1/library/books"):
            libraryAdded = true; value = libraryBook
        case (.get, "/api/v1/library"):
            value = libraryAdded ? [libraryBook] : [LibraryBook]()
        case (.patch, "/api/v1/library/books/\(libraryId)"):
            value = libraryBook
        case (.get, "/api/v1/reading-goals"), (.put, "/api/v1/reading-goals"):
            value = ReadingGoal(dailyMinutes: 20, weeklyDays: 5, dailyPages: nil)
        case (.get, "/api/v1/statistics/summary"):
            value = StatisticsSummary(minutesToday: 12, minutesThisWeek: 90, pagesThisWeek: 64,
                                      daysReadLast30: 18, currentStreak: 7, bestStreak: 12,
                                      finishedBooksThisYear: 3, topGenres: ["Ficção": 2], totalSessions: 14, pacePercent: 72)
        case (.post, "/api/v1/reading-sessions"):
            value = ReadingSession(id: sessionId, userId: userId, userBookId: libraryId, startPage: 96,
                                   endPage: nil, goalPages: nil, startedAt: "2026-07-13T12:00:00Z",
                                   finishedAt: nil, durationSeconds: nil, pagesRead: nil, note: nil)
        case (.patch, "/api/v1/reading-sessions/\(sessionId)/finish"):
            value = SessionSummary(sessionId: sessionId, durationMinutes: 24, pagesRead: 12,
                                   progressPercent: 35, averagePagesPerHour: 30, estimatedSessionsRemaining: 17,
                                   estimatedFinishDate: "2026-08-02", currentStreak: 8)
        case (.get, "/api/v1/posts/feed"), (.get, "/api/v1/posts/discover"):
            value = [post]
        case (.get, "/api/v1/posts/\(postId)/comments"):
            value = [comment]
        case (.get, "/api/v1/shelves"):
            value = [Shelf]()
        default:
            throw APIError.server(status: 404, message: "Stub XCUITest ausente: \(endpoint.method.rawValue) \(endpoint.path)")
        }
        let data = try JSONEncoder().encode(AnyEncodable(value))
        return try JSONDecoder().decode(T.self, from: data)
    }

    func sendVoid(_ endpoint: Endpoint) async throws { }

    private var book: Book {
        Book(workId: workId, editionId: editionId, title: "A Biblioteca da Meia-Noite",
             description: "Uma história sobre escolhas e possibilidades.", authors: ["Matt Haig"], categories: ["Ficção"],
             isbn10: nil, isbn13: "9786555602079", publisher: "Bertrand Brasil", publicationDate: "2021-01-01",
             language: "pt", pageCount: 308, coverUrl: nil, externalProvider: "UI_TEST", externalId: "demo")
    }

    private var libraryBook: LibraryBook {
        LibraryBook(id: libraryId, userId: userId, editionId: editionId, status: .reading, currentPage: 96,
                    startedAt: "2026-07-01T12:00:00Z", finishedAt: nil, rating: nil, targetFinishDate: nil,
                    createdAt: "2026-07-01T12:00:00Z", updatedAt: "2026-07-13T12:00:00Z")
    }

    private var post: Post {
        Post(id: postId, authorId: userId, text: "Uma leitura acolhedora sobre recomeços. Recomendo!",
             editionId: editionId, type: .recommendation, spoiler: false, spoilerPage: nil,
             visibility: .publicPost, createdAt: "2026-07-14T12:00:00Z", updatedAt: "2026-07-14T12:00:00Z",
             likeCount: 1, commentCount: 1)
    }

    private var comment: Comment {
        Comment(id: commentId, postId: postId, authorId: userId, text: "Concordo!",
                createdAt: "2026-07-14T12:05:00Z")
    }
}

private struct AnyEncodable: Encodable {
    private let encodeValue: (Encoder) throws -> Void
    init(_ value: any Encodable) { encodeValue = value.encode }
    func encode(to encoder: Encoder) throws { try encodeValue(encoder) }
}
#endif

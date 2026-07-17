import Foundation

struct TokenPair: Codable, Sendable, Equatable {
    let accessToken: String
    let refreshToken: String
    let tokenType: String
    let expiresIn: Int
}

struct UserProfile: Codable, Identifiable, Sendable, Equatable {
    let id: UUID
    let name: String
    let username: String
    let email: String?
    let bio: String
    let avatarUrl: String?
    let publicProfile: Bool
    let followers: Int
    let following: Int
    let finishedBooks: Int
    let totalReadingMinutes: Int
    let currentStreak: Int
    let createdAt: String
}

enum FollowStatus: String, Codable, Sendable, Equatable {
    case pending = "PENDING"
    case accepted = "ACCEPTED"
}

struct FollowStatusResponse: Codable, Sendable, Equatable {
    let status: FollowStatus?
}

struct FollowRequest: Codable, Identifiable, Sendable, Equatable {
    let followerId: UUID
    let followedId: UUID
    let status: FollowStatus
    let createdAt: String

    var id: UUID { followerId }
}

enum CommunityActivityType: String, Codable, Sendable, Equatable {
    case followRequest = "FOLLOW_REQUEST"
    case followAccepted = "FOLLOW_ACCEPTED"
    case userFollowed = "USER_FOLLOWED"
    case postLiked = "POST_LIKED"
    case postCommented = "POST_COMMENTED"
}

struct CommunityActivity: Codable, Identifiable, Sendable, Equatable {
    let id: UUID
    let recipientId: UUID
    let actorId: UUID
    let type: CommunityActivityType
    let postId: UUID?
    let read: Bool
    let createdAt: String
}

struct CommunityAccess: Codable, Sendable, Equatable {
    let allowed: Bool
    let suspendedUntil: String?
    let reason: String?
}

struct ProfilePhotoUpload: Codable, Sendable, Equatable {
    let objectKey: String
    let uploadUrl: URL
    let expiresAt: String
}

struct Book: Codable, Identifiable, Sendable, Equatable, Hashable {
    let workId: UUID
    let editionId: UUID
    let title: String
    let description: String
    let authors: [String]
    let categories: [String]
    let isbn10: String?
    let isbn13: String?
    let publisher: String?
    let publicationDate: String?
    let language: String
    let pageCount: Int?
    let coverUrl: String?
    let externalProvider: String?
    let externalId: String?

    var id: UUID { editionId }
    var authorLine: String { authors.joined(separator: ", ") }
}

enum ReadingStatus: String, Codable, CaseIterable, Sendable {
    case wantToRead = "WANT_TO_READ"
    case reading = "READING"
    case paused = "PAUSED"
    case finished = "FINISHED"
    case abandoned = "ABANDONED"

    var title: String {
        switch self {
        case .wantToRead: "Quero ler"
        case .reading: "Lendo"
        case .paused: "Pausado"
        case .finished: "Finalizado"
        case .abandoned: "Abandonado"
        }
    }
}

struct LibraryBook: Codable, Identifiable, Sendable, Equatable, Hashable {
    let id: UUID
    let userId: UUID
    let editionId: UUID
    let status: ReadingStatus
    let currentPage: Int
    let startedAt: String?
    let finishedAt: String?
    let rating: Int?
    let targetFinishDate: String?
    let createdAt: String
    let updatedAt: String
}

struct ReadingGoal: Codable, Sendable, Equatable {
    let dailyMinutes: Int
    let weeklyDays: Int
    let dailyPages: Int?
}

struct ReadingSession: Codable, Identifiable, Sendable, Equatable {
    let id: UUID
    let userId: UUID
    let userBookId: UUID
    let startPage: Int
    let currentPage: Int
    let endPage: Int?
    let goalPages: Int?
    let startedAt: String
    let pausedAt: String?
    let pausedSeconds: Int
    let finishedAt: String?
    let durationSeconds: Int?
    let pagesRead: Int?
    let note: String?
}

struct SessionSummary: Codable, Sendable, Equatable, Hashable {
    let sessionId: UUID
    let durationMinutes: Int
    let pagesRead: Int
    let progressPercent: Double
    let averagePagesPerHour: Double
    let estimatedSessionsRemaining: Int
    let estimatedFinishDate: String?
    let currentStreak: Int
}

enum PostType: String, Codable, CaseIterable, Sendable {
    case note = "NOTE"
    case review = "REVIEW"
    case recommendation = "RECOMMENDATION"
    case progress = "PROGRESS"
    case quote = "QUOTE"
}

enum PostVisibility: String, Codable, CaseIterable, Sendable {
    case publicPost = "PUBLIC"
    case followers = "FOLLOWERS"
    case privatePost = "PRIVATE"
}

struct Post: Codable, Identifiable, Sendable, Equatable, Hashable {
    let id: UUID
    let authorId: UUID
    let text: String
    let editionId: UUID?
    let type: PostType
    let spoiler: Bool
    let spoilerPage: Int?
    let visibility: PostVisibility
    let createdAt: String
    let updatedAt: String
    let likeCount: Int
    let commentCount: Int

    func updatingEngagement(likeCount: Int? = nil, commentCount: Int? = nil) -> Post {
        Post(
            id: id,
            authorId: authorId,
            text: text,
            editionId: editionId,
            type: type,
            spoiler: spoiler,
            spoilerPage: spoilerPage,
            visibility: visibility,
            createdAt: createdAt,
            updatedAt: updatedAt,
            likeCount: likeCount ?? self.likeCount,
            commentCount: commentCount ?? self.commentCount
        )
    }
}

struct Comment: Codable, Identifiable, Sendable, Equatable {
    let id: UUID
    let postId: UUID
    let authorId: UUID
    let text: String
    let createdAt: String
}

struct StatisticsSummary: Codable, Sendable, Equatable {
    let minutesToday: Int
    let minutesThisWeek: Int
    let pagesThisWeek: Int
    let daysReadLast30: Int
    let currentStreak: Int
    let bestStreak: Int
    let finishedBooksThisYear: Int
    let topGenres: [String: Int]
    let totalSessions: Int
    let pacePercent: Double
}

struct Shelf: Codable, Identifiable, Sendable, Equatable {
    let id: UUID
    let userId: UUID
    let name: String
    let publicShelf: Bool
    let createdAt: String
    let updatedAt: String
}

struct Note: Codable, Identifiable, Sendable, Equatable {
    let id: UUID
    let userId: UUID
    let editionId: UUID
    let content: String
    let page: Int?
    let chapter: String?
    let privateNote: Bool
    let spoiler: Bool
    let postId: UUID?
    let createdAt: String
    let updatedAt: String
}

struct Review: Codable, Identifiable, Sendable, Equatable {
    let id: UUID
    let userId: UUID
    let workId: UUID
    let editionId: UUID?
    let rating: Int
    let title: String?
    let text: String
    let spoiler: Bool
    let postId: UUID?
    let createdAt: String
    let updatedAt: String
}

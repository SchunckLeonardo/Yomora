import XCTest
@testable import Yomora

@MainActor
final class SessionStoreTests: XCTestCase {
    func testLoginStoresTokensAndLoadsTheProfile() async throws {
        let tokens = TokenPair(accessToken: "access", refreshToken: "refresh", tokenType: "Bearer", expiresIn: 900)
        let user = UserProfile(id: UUID(), name: "Marina", username: "marina", email: "marina@example.com",
                               bio: "", avatarUrl: nil, publicProfile: true, followers: 0, following: 0,
                               finishedBooks: 0, totalReadingMinutes: 0, currentStreak: 0, createdAt: "2026-07-13T12:00:00Z")
        let api = StubAPIClient(responses: ["/api/v1/auth/login": tokens.encoded, "/api/v1/users/me": user.encoded])
        let tokenStore = InMemoryTokenStore()
        let session = SessionStore(api: api, tokens: tokenStore)

        let loggedIn = await session.login(email: "marina@example.com", password: "segura123")
        let stored = await tokenStore.load()
        XCTAssertTrue(loggedIn)
        XCTAssertEqual(session.state, .signedIn)
        XCTAssertEqual(session.user, user)
        XCTAssertEqual(stored, tokens)
    }
}

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

    func testAppleSignInStoresYomoraTokensAndLoadsPendingProfile() async throws {
        let tokens = TokenPair(accessToken: "apple-access", refreshToken: "refresh", tokenType: "Bearer", expiresIn: 900)
        let user = UserProfile(
            id: UUID(), name: "Marina Apple", username: "leitor-temp", email: "reader@example.com",
            emailVerified: true, profileComplete: false, bio: "", avatarUrl: nil, publicProfile: true,
            followers: 0, following: 0, finishedBooks: 0, totalReadingMinutes: 0, currentStreak: 0,
            createdAt: "2026-07-16T12:00:00Z"
        )
        let api = StubAPIClient(responses: ["/api/v1/auth/apple": tokens.encoded, "/api/v1/users/me": user.encoded])
        let tokenStore = InMemoryTokenStore()
        let session = SessionStore(api: api, tokens: tokenStore)

        let loggedIn = await session.appleSignIn(AppleAuthorizationPayload(
            identityToken: "identity-token",
            authorizationCode: "authorization-code",
            nonce: "raw-nonce",
            fullName: "Marina Apple"
        ))

        XCTAssertTrue(loggedIn)
        XCTAssertEqual(session.state, .signedIn)
        XCTAssertEqual(session.user, user)
        let stored = await tokenStore.load()
        XCTAssertEqual(stored, tokens)
    }

    func testCompletingTheAppleProfileReplacesThePendingProfile() async throws {
        let completed = UserProfile(
            id: UUID(), name: "Marina", username: "marina", email: "reader@example.com",
            emailVerified: true, profileComplete: true, bio: "", avatarUrl: nil, publicProfile: true,
            followers: 0, following: 0, finishedBooks: 0, totalReadingMinutes: 0, currentStreak: 0,
            createdAt: "2026-07-16T12:00:00Z"
        )
        let api = StubAPIClient(responses: ["/api/v1/users/me": completed.encoded])
        let session = SessionStore(api: api, tokens: InMemoryTokenStore())

        let saved = await session.completeProfile(name: "Marina", username: "marina")

        XCTAssertTrue(saved)
        XCTAssertEqual(session.user, completed)
    }

    func testEmailVerificationLinkConfirmsTheTokenAndRefreshesTheProfile() async throws {
        let verified = UserProfile(
            id: UUID(), name: "Marina", username: "marina", email: "reader@example.com",
            emailVerified: true, profileComplete: true, bio: "", avatarUrl: nil, publicProfile: true,
            followers: 0, following: 0, finishedBooks: 0, totalReadingMinutes: 0, currentStreak: 0,
            createdAt: "2026-07-16T12:00:00Z"
        )
        let api = StubAPIClient(responses: ["/api/v1/users/me": verified.encoded])
        let session = SessionStore(api: api, tokens: InMemoryTokenStore())
        let url = try XCTUnwrap(URL(string: "https://api.yomora.app/api/v1/auth/email-verification/confirm?token=abc"))

        await session.handleEmailVerificationURL(url)

        XCTAssertEqual(session.user, verified)
        let paths = await api.paths
        XCTAssertEqual(paths, ["/api/v1/auth/email-verification/confirm", "/api/v1/users/me"])
    }

    func testDeletingTheAccountClearsTheLocalSession() async throws {
        let tokenStore = InMemoryTokenStore()
        await tokenStore.save(TokenPair(
            accessToken: "access", refreshToken: "refresh", tokenType: "Bearer", expiresIn: 900
        ))
        let api = StubAPIClient(responses: [:])
        let session = SessionStore(api: api, tokens: tokenStore)

        let deleted = await session.deleteAccount()
        XCTAssertTrue(deleted)

        let stored = await tokenStore.load()
        XCTAssertNil(stored)
        XCTAssertEqual(session.state, .signedOut)
        let paths = await api.paths
        XCTAssertEqual(paths, ["/api/v1/users/me"])
    }
}

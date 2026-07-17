import XCTest
@testable import Yomora

@MainActor
final class ProfileViewModelTests: XCTestCase {
    func testPrivateFollowRequestShowsPendingAndCanBeCancelled() async {
        let userId = UUID()
        let api = StubAPIClient(responses: [
            "/api/v1/users/\(userId)/follow": FollowStatusResponse(status: .pending).encoded
        ])
        let model = ProfileViewModel(userId: userId, isCurrentUser: false, api: api)

        await model.toggleFollow()
        XCTAssertEqual(model.followStatus, .pending)
        XCTAssertEqual(model.followButtonTitle, "Solicitado")

        await model.toggleFollow()
        XCTAssertNil(model.followStatus)
        let paths = await api.paths
        XCTAssertEqual(paths, [
            "/api/v1/users/\(userId)/follow",
            "/api/v1/users/\(userId)/follow"
        ])
    }

    func testApprovingRequestRemovesItFromInbox() async {
        let followerId = UUID()
        let ownerId = UUID()
        let request = FollowRequest(
            followerId: followerId,
            followedId: ownerId,
            status: .pending,
            createdAt: "2026-07-13T18:00:00Z"
        )
        let profile = UserProfile(
            id: ownerId,
            name: "Leitora",
            username: "leitora",
            email: "leitora@example.com",
            bio: "",
            avatarUrl: nil,
            publicProfile: false,
            followers: 0,
            following: 0,
            finishedBooks: 0,
            totalReadingMinutes: 0,
            currentStreak: 0,
            createdAt: "2026-07-13T18:00:00Z"
        )
        let api = StubAPIClient(responses: [
            "/api/v1/users/me": profile.encoded,
            "/api/v1/users/me/follow-requests": [request].encoded
        ])
        let model = ProfileViewModel(userId: ownerId, isCurrentUser: true, api: api)

        await model.load()
        XCTAssertEqual(model.pendingRequests, [request])

        await model.approve(request)
        XCTAssertTrue(model.pendingRequests.isEmpty)
    }

    func testReportingAProfileDoesNotBlockIt() async {
        let userId = UUID()
        let api = StubAPIClient(responses: [:])
        let model = ProfileViewModel(userId: userId, isCurrentUser: false, api: api)

        await model.report(reason: "Assédio", details: "Mensagem ofensiva")

        let paths = await api.paths
        XCTAssertEqual(paths, ["/api/v1/moderation/reports"])
        XCTAssertTrue(model.didSubmitReport)
    }
}

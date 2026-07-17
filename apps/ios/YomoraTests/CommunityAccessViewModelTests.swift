import XCTest
@testable import Yomora

@MainActor
final class CommunityAccessViewModelTests: XCTestCase {
    func testSuspensionIsShownWithoutLoadingTheFeed() async {
        let access = CommunityAccess(
            allowed: false,
            suspendedUntil: "2026-07-24T12:00:00Z",
            reason: "Assédio"
        )
        let api = StubAPIClient(responses: [
            "/api/v1/community/access": access.encoded
        ])
        let model = CommunityAccessViewModel(api: api)

        await model.load()

        XCTAssertEqual(model.state, .suspended(access))
        let paths = await api.paths
        XCTAssertEqual(paths, ["/api/v1/community/access"])
    }
}

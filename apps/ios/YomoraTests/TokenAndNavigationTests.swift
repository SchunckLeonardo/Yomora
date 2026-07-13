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

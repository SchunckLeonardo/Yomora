import XCTest
@testable import Yomora

final class AppleSignInNonceTests: XCTestCase {
    func testHashMatchesTheNonceFormatExpectedByApple() {
        XCTAssertEqual(
            AppleSignInNonce.sha256("abc"),
            "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"
        )
    }

    func testGeneratedNonceHasEnoughEntropyAndUsesURLSafeCharacters() throws {
        let nonce = try AppleSignInNonce.random(length: 32)
        XCTAssertEqual(nonce.count, 32)
        XCTAssertNotNil(nonce.range(of: "^[0-9A-Za-z._-]+$", options: .regularExpression))
    }
}

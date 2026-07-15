import XCTest
@testable import Yomora

@MainActor
final class TextDraftTests: XCTestCase {
    func testDraftKeepsLatestTextForDeferredSubmission() {
        let draft = TextDraft()

        draft.value = "leonardo@yomora.app"

        XCTAssertEqual(draft.value, "leonardo@yomora.app")
    }

    func testDraftTreatsWhitespaceOnlyInputAsBlank() {
        let draft = TextDraft("  \n ")

        XCTAssertTrue(draft.isBlank)

        draft.value = "Leitura concluída"

        XCTAssertFalse(draft.isBlank)
    }
}

import XCTest

final class YomoraFlowUITests: XCTestCase {
    @MainActor
    func testLoginSearchAddAndFinishReadingSession() {
        let app = XCUIApplication()
        app.launchArguments = ["--ui-testing", "--skip-onboarding"]
        app.launch()

        let email = app.textFields["emailField"]
        XCTAssertTrue(email.waitForExistence(timeout: 3))
        email.tap(); email.typeText("marina@yomora.local")
        let password = app.secureTextFields["passwordField"]
        password.tap(); password.typeText("Yomora123!")
        app.buttons["authenticationButton"].tap()

        XCTAssertTrue(app.tabBars.buttons["Descobrir"].waitForExistence(timeout: 3))
        app.tabBars.buttons["Descobrir"].tap()
        let search = app.textFields["bookSearchField"]
        XCTAssertTrue(search.waitForExistence(timeout: 3))
        search.tap(); search.typeText("biblioteca"); search.typeKey("\n", modifierFlags: [])
        XCTAssertTrue(app.staticTexts["A Biblioteca da Meia-Noite"].waitForExistence(timeout: 3))
        app.staticTexts["A Biblioteca da Meia-Noite"].tap()
        let add = app.buttons["addToLibraryButton"]
        XCTAssertTrue(add.waitForExistence(timeout: 2)); add.tap()

        app.tabBars.buttons["Biblioteca"].tap()
        XCTAssertTrue(app.staticTexts["A Biblioteca da Meia-Noite"].waitForExistence(timeout: 3))
        app.staticTexts["A Biblioteca da Meia-Noite"].tap()
        app.buttons["startReadingSessionButton"].tap()
        app.buttons["timerStartButton"].tap()
        XCTAssertTrue(app.buttons["timerFinishButton"].waitForExistence(timeout: 2))
        let note = app.textFields["readingSessionNoteField"]
        XCTAssertTrue(note.waitForExistence(timeout: 2))
        note.tap(); note.typeText("Sessão tranquila.")
        XCTAssertEqual(note.value as? String, "Sessão tranquila.")
        app.buttons["timerFinishButton"].tap()
        XCTAssertTrue(app.staticTexts["Sessão concluída"].waitForExistence(timeout: 3))
    }

    @MainActor
    func testCommentComposerStaysAboveSoftwareKeyboard() throws {
        let app = XCUIApplication()
        app.launchArguments = ["--ui-testing", "--skip-onboarding"]
        app.launch()

        let email = app.textFields["emailField"]
        XCTAssertTrue(email.waitForExistence(timeout: 3))
        email.tap(); email.typeText("keyboard@yomora.local")
        let password = app.secureTextFields["passwordField"]
        password.tap(); password.typeText("Yomora123!")
        app.buttons["authenticationButton"].tap()

        let community = app.tabBars.buttons["Comunidade"]
        XCTAssertTrue(community.waitForExistence(timeout: 3)); community.tap()
        let comments = app.buttons["Abrir comentários"]
        XCTAssertTrue(comments.waitForExistence(timeout: 3)); comments.tap()

        let field = app.textFields["commentField"]
        XCTAssertTrue(field.waitForExistence(timeout: 3)); field.tap()
        field.typeText("O campo continua visível.")

        let keyboard = app.keyboards.firstMatch
        guard keyboard.waitForExistence(timeout: 2) else {
            throw XCTSkip("O runner está usando teclado físico")
        }
        XCTAssertLessThanOrEqual(field.frame.maxY, keyboard.frame.minY + 1)
        XCTAssertTrue(app.buttons["sendCommentButton"].isHittable)
    }
}

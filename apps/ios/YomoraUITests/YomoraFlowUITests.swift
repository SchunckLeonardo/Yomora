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
        app.buttons["timerFinishButton"].tap()
        XCTAssertTrue(app.staticTexts["Sessão concluída"].waitForExistence(timeout: 3))
    }
}

import XCTest

final class YomoraFlowUITests: XCTestCase {
    @MainActor
    func testLoginSearchAddAndFinishReadingSession() {
        let app = launchAuthenticatedApp(email: "marina@yomora.local")

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
        let note = app.textViews["readingSessionNoteField"]
        XCTAssertTrue(note.waitForExistence(timeout: 2))
        note.tap(); note.typeText("Sessão tranquila.")
        XCTAssertEqual(note.value as? String, "Sessão tranquila.")
        let keyboard = app.keyboards.firstMatch
        if keyboard.waitForExistence(timeout: 2) {
            XCTAssertLessThanOrEqual(note.frame.maxY, keyboard.frame.minY + 1)
            let dismissKeyboard = app.buttons["dismissReadingNoteKeyboardButton"]
            XCTAssertTrue(dismissKeyboard.waitForExistence(timeout: 2))
            dismissKeyboard.tap()
        }
        app.buttons["timerFinishButton"].tap()
        XCTAssertTrue(app.staticTexts["Sessão concluída"].waitForExistence(timeout: 3))
        XCTAssertTrue(app.staticTexts["Sessão tranquila."].waitForExistence(timeout: 2))
        let exitSummary = app.buttons["Voltar para Hoje"]
        XCTAssertTrue(exitSummary.waitForExistence(timeout: 2))
        exitSummary.tap()
        XCTAssertTrue(app.tabBars.buttons["Hoje"].isSelected)
        XCTAssertTrue(app.navigationBars["Yomora"].waitForExistence(timeout: 3))

        app.tabBars.buttons["Biblioteca"].tap()
        XCTAssertTrue(app.staticTexts["A Biblioteca da Meia-Noite"].waitForExistence(timeout: 3))
        app.staticTexts["A Biblioteca da Meia-Noite"].tap()
        XCTAssertTrue(app.staticTexts["Notas das sessões"].waitForExistence(timeout: 3))
        XCTAssertTrue(app.staticTexts["Sessão tranquila."].waitForExistence(timeout: 2))
    }

    @MainActor
    func testCommentComposerStaysAboveSoftwareKeyboard() throws {
        let app = launchAuthenticatedApp(email: "keyboard@yomora.local")

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

    @MainActor
    func testCommunityNavigationActivityBadgeDatesAndBookLayout() {
        let app = launchAuthenticatedApp(email: "community@yomora.local")

        app.tabBars.buttons["Comunidade"].tap()
        let author = app.buttons["postAuthorProfileButton_70000000-0000-0000-0000-000000000001"]
        XCTAssertTrue(author.waitForExistence(timeout: 3))
        author.tap()
        XCTAssertTrue(app.navigationBars["Perfil"].waitForExistence(timeout: 3))
        app.navigationBars.buttons.firstMatch.tap()

        let comments = app.buttons["Abrir comentários"]
        XCTAssertTrue(comments.waitForExistence(timeout: 3))
        comments.tap()
        let commentAuthor = app.buttons["commentAuthorProfileButton_80000000-0000-0000-0000-000000000001"]
        XCTAssertTrue(commentAuthor.waitForExistence(timeout: 3))
        commentAuthor.tap()
        XCTAssertTrue(app.navigationBars["Perfil"].waitForExistence(timeout: 3))
        app.navigationBars.buttons.firstMatch.tap()
        XCTAssertTrue(app.navigationBars["Publicação"].waitForExistence(timeout: 3))
        app.navigationBars.buttons.firstMatch.tap()

        let inbox = app.buttons["activityInboxButton"]
        XCTAssertTrue(inbox.waitForExistence(timeout: 3))
        expectation(
            for: NSPredicate(format: "value == %@", "2 não lidas"),
            evaluatedWith: inbox
        )
        waitForExpectations(timeout: 3)
        inbox.tap()

        let rawISODate = app.staticTexts.matching(
            NSPredicate(format: "label MATCHES %@", ".*T[0-9]{2}:[0-9]{2}:[0-9]{2}.*Z")
        ).firstMatch
        XCTAssertFalse(rawISODate.waitForExistence(timeout: 1))

        let follow = app.buttons["activityRow_71000000-0000-0000-0000-000000000001"]
        XCTAssertTrue(follow.waitForExistence(timeout: 3))
        follow.tap()
        XCTAssertTrue(app.navigationBars["Perfil"].waitForExistence(timeout: 3))
        app.navigationBars.buttons.firstMatch.tap()

        XCTAssertTrue(inbox.waitForExistence(timeout: 3))
        inbox.tap()
        let postActivity = app.buttons["activityRow_72000000-0000-0000-0000-000000000001"]
        XCTAssertTrue(postActivity.waitForExistence(timeout: 3))
        postActivity.tap()
        XCTAssertTrue(app.navigationBars["Publicação"].waitForExistence(timeout: 3))
        app.navigationBars.buttons.firstMatch.tap()

        app.tabBars.buttons["Descobrir"].tap()
        let search = app.textFields["bookSearchField"]
        XCTAssertTrue(search.waitForExistence(timeout: 3))
        search.tap(); search.typeText("biblioteca"); search.typeKey("\n", modifierFlags: [])
        app.staticTexts["A Biblioteca da Meia-Noite"].tap()

        let content = app.otherElements["bookDetailsContent"]
        XCTAssertTrue(content.waitForExistence(timeout: 3))
        let title = app.staticTexts["bookDetailsTitle"]
        XCTAssertTrue(title.waitForExistence(timeout: 3))
        let titleOriginBeforeAdding = title.frame.origin
        app.buttons["addToLibraryButton"].tap()
        XCTAssertTrue(app.staticTexts["Adicionado à sua biblioteca"].waitForExistence(timeout: 3))
        XCTAssertEqual(title.frame.origin.x, titleOriginBeforeAdding.x, accuracy: 1)
        XCTAssertEqual(title.frame.origin.y, titleOriginBeforeAdding.y, accuracy: 1)
    }

    @MainActor
    private func launchAuthenticatedApp(email address: String) -> XCUIApplication {
        let app = XCUIApplication()
        app.launchArguments = ["--ui-testing", "--skip-onboarding"]
        app.launch()

        let email = app.textFields["emailField"]
        XCTAssertTrue(email.waitForExistence(timeout: 3))
        email.tap(); email.typeText(address)
        let password = app.secureTextFields["passwordField"]
        password.tap(); password.typeText("Yomora123!")
        app.buttons["authenticationButton"].tap()

        XCTAssertTrue(app.tabBars.firstMatch.waitForExistence(timeout: 10))
        return app
    }
}

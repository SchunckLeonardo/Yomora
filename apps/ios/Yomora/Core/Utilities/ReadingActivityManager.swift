import Foundation

protocol ReadingActivityManaging: Sendable {
    func start(bookTitle: String, startedAt: Date) async
    func end() async
}

struct NoopReadingActivityManager: ReadingActivityManaging {
    func start(bookTitle: String, startedAt: Date) async { }
    func end() async { }
}

// A porta acima mantém a feature independente de entitlements. O adapter ActivityKit
// entra aqui quando o target de Live Activity for habilitado no time Apple.


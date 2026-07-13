import Foundation
import SwiftData

@Model
final class CachedBook {
    @Attribute(.unique) var editionId: UUID
    var payload: Data
    var cachedAt: Date

    init(editionId: UUID, payload: Data, cachedAt: Date = .now) {
        self.editionId = editionId
        self.payload = payload
        self.cachedAt = cachedAt
    }
}

@MainActor
final class BookCache {
    private let context: ModelContext

    init(container: ModelContainer) { context = ModelContext(container) }

    func store(_ books: [Book]) throws {
        for book in books {
            context.insert(CachedBook(editionId: book.editionId, payload: try JSONEncoder().encode(book)))
        }
        try context.save()
    }

    func recent() -> [Book] {
        let descriptor = FetchDescriptor<CachedBook>(sortBy: [SortDescriptor(\.cachedAt, order: .reverse)])
        return (try? context.fetch(descriptor))?.compactMap { try? JSONDecoder().decode(Book.self, from: $0.payload) } ?? []
    }
}


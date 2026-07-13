import SwiftData
import XCTest
@testable import Yomora

@MainActor
final class SearchViewModelTests: XCTestCase {
    func testSearchLoadsBooksAndCachesThem() async throws {
        let book = Book(workId: UUID(), editionId: UUID(), title: "O Hobbit", description: "", authors: ["J.R.R. Tolkien"],
                        categories: ["Fantasia"], isbn10: nil, isbn13: nil, publisher: nil, publicationDate: nil,
                        language: "pt", pageCount: 336, coverUrl: nil, externalProvider: "test", externalId: "1")
        let api = StubAPIClient(responses: ["/api/v1/books/search": [book].encoded])
        let container = try ModelContainer(for: CachedBook.self, configurations: ModelConfiguration(isStoredInMemoryOnly: true))
        let cache = BookCache(container: container)
        let viewModel = SearchViewModel(api: api, cache: cache)
        viewModel.query = "hobbit"

        await viewModel.search()

        XCTAssertEqual(viewModel.state, .loaded)
        XCTAssertEqual(viewModel.books, [book])
        XCTAssertEqual(cache.recent(), [book])
    }
}


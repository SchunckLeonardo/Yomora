import Foundation
import Observation

@MainActor
@Observable
final class SearchViewModel {
    enum State: Equatable { case idle, loading, loaded, error(String) }
    var query = ""
    var language = "pt"
    private(set) var state: State = .idle
    private(set) var books: [Book] = []
    private let api: any APIClientProtocol
    private let cache: BookCache

    init(api: any APIClientProtocol, cache: BookCache) {
        self.api = api
        self.cache = cache
    }

    func search() async {
        let value = query.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !value.isEmpty else { state = .idle; books = []; return }
        state = .loading
        do {
            let endpoint = Endpoint(path: "/api/v1/books/search", query: [
                URLQueryItem(name: "q", value: value), URLQueryItem(name: "language", value: language)
            ], authenticated: false)
            books = try await api.send(endpoint, as: [Book].self)
            try? cache.store(books)
            state = .loaded
        } catch {
            let cached = cache.recent().filter { $0.title.localizedCaseInsensitiveContains(value) || $0.authorLine.localizedCaseInsensitiveContains(value) }
            if cached.isEmpty { state = .error(error.localizedDescription) }
            else { books = cached; state = .loaded }
        }
    }
}


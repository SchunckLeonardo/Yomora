import Foundation
import Observation

struct LibraryItem: Identifiable, Equatable {
    let entry: LibraryBook
    let book: Book
    var id: UUID { entry.id }
}

@MainActor
@Observable
final class LibraryViewModel {
    enum State: Equatable { case idle, loading, loaded, error(String) }
    var filter: ReadingStatus?
    private(set) var state: State = .idle
    private(set) var items: [LibraryItem] = []
    private let api: any APIClientProtocol

    init(api: any APIClientProtocol) { self.api = api }

    func load() async {
        state = .loading
        do {
            var endpoint = Endpoint(path: "/api/v1/library")
            if let filter { endpoint.query = [URLQueryItem(name: "status", value: filter.rawValue)] }
            let entries = try await api.send(endpoint, as: [LibraryBook].self)
            var loaded: [LibraryItem] = []
            for entry in entries {
                if let book = try? await api.send(Endpoint(path: "/api/v1/books/\(entry.editionId)", authenticated: false), as: Book.self) {
                    loaded.append(LibraryItem(entry: entry, book: book))
                }
            }
            items = loaded
            state = .loaded
        } catch { state = .error(error.localizedDescription) }
    }
}


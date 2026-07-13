import Foundation
import Observation

@MainActor
@Observable
final class FeedViewModel {
    enum State: Equatable { case idle, loading, loaded, error(String) }
    enum FeedKind: String, CaseIterable { case following = "Seguindo", discover = "Descobrir" }
    var kind: FeedKind = .following
    private(set) var state: State = .idle
    private(set) var posts: [Post] = []
    private let api: any APIClientProtocol

    init(api: any APIClientProtocol) { self.api = api }

    func load() async {
        state = .loading
        do {
            let path = kind == .following ? "/api/v1/posts/feed" : "/api/v1/posts/discover"
            posts = try await api.send(Endpoint(path: path), as: [Post].self)
            state = .loaded
        } catch { state = .error(error.localizedDescription) }
    }

    func like(_ post: Post) async {
        var endpoint = Endpoint(path: "/api/v1/posts/\(post.id)/likes", method: .post)
        endpoint.body = Data("{}".utf8)
        if let updated = try? await api.send(endpoint, as: Post.self),
           let index = posts.firstIndex(where: { $0.id == post.id }) { posts[index] = updated }
    }
}


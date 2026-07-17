import Foundation
import Observation

@MainActor
@Observable
final class FeedViewModel {
    enum State: Equatable { case idle, loading, loaded, error(String) }
    enum FeedKind: String, CaseIterable { case following = "Seguindo", discover = "Em destaque" }
    var kind: FeedKind = .following
    private(set) var state: State = .idle
    private(set) var posts: [Post] = []
    private(set) var likedPostIDs: Set<UUID> = []
    private(set) var interactionError: String?
    private var pendingLikeIDs: Set<UUID> = []
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

    func isLiked(_ post: Post) -> Bool {
        likedPostIDs.contains(post.id)
    }

    func toggleLike(_ post: Post) async {
        guard pendingLikeIDs.insert(post.id).inserted,
              let index = posts.firstIndex(where: { $0.id == post.id }) else { return }
        defer { pendingLikeIDs.remove(post.id) }
        let original = posts[index]
        let shouldLike = !isLiked(post)
        let optimisticCount = max(0, original.likeCount + (shouldLike ? 1 : -1))
        posts[index] = original.updatingEngagement(likeCount: optimisticCount)
        if shouldLike { likedPostIDs.insert(post.id) }
        else { likedPostIDs.remove(post.id) }

        var endpoint = Endpoint(path: "/api/v1/posts/\(post.id)/likes", method: shouldLike ? .post : .delete)
        if shouldLike { endpoint.body = Data("{}".utf8) }
        do {
            let updated = try await api.send(endpoint, as: Post.self)
            apply(updated, liked: shouldLike)
            interactionError = nil
        } catch {
            if let rollbackIndex = posts.firstIndex(where: { $0.id == post.id }) {
                posts[rollbackIndex] = original
            }
            if shouldLike { likedPostIDs.remove(post.id) }
            else { likedPostIDs.insert(post.id) }
            interactionError = error.localizedDescription
        }
    }

    func apply(_ updated: Post, liked: Bool? = nil) {
        if let index = posts.firstIndex(where: { $0.id == updated.id }) { posts[index] = updated }
        guard let liked else { return }
        if liked { likedPostIDs.insert(updated.id) }
        else { likedPostIDs.remove(updated.id) }
    }
}

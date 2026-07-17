import Foundation
import Observation

@MainActor
@Observable
final class ProfileViewModel {
    private(set) var profile: UserProfile?
    private(set) var followStatus: FollowStatus?
    private(set) var pendingRequests: [FollowRequest] = []
    private(set) var errorMessage: String?

    let userId: UUID
    let isCurrentUser: Bool
    private let api: any APIClientProtocol

    init(userId: UUID, isCurrentUser: Bool, api: any APIClientProtocol) {
        self.userId = userId
        self.isCurrentUser = isCurrentUser
        self.api = api
    }

    var followButtonTitle: String {
        switch followStatus {
        case .accepted: "Seguindo"
        case .pending: "Solicitado"
        case nil: "Seguir"
        }
    }

    func load() async {
        errorMessage = nil
        do {
            profile = try await api.send(
                Endpoint(path: isCurrentUser ? "/api/v1/users/me" : "/api/v1/users/\(userId)"),
                as: UserProfile.self
            )
            if isCurrentUser {
                pendingRequests = try await api.send(
                    Endpoint(path: "/api/v1/users/me/follow-requests"),
                    as: [FollowRequest].self
                )
            } else {
                followStatus = try await api.send(
                    Endpoint(path: "/api/v1/users/\(userId)/follow-status"),
                    as: FollowStatusResponse.self
                ).status
            }
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    func toggleFollow() async {
        errorMessage = nil
        do {
            if followStatus == nil {
                var endpoint = Endpoint(path: "/api/v1/users/\(userId)/follow", method: .post)
                endpoint.body = Data("{}".utf8)
                followStatus = try await api.send(endpoint, as: FollowStatusResponse.self).status
            } else {
                try await api.sendVoid(Endpoint(path: "/api/v1/users/\(userId)/follow", method: .delete))
                followStatus = nil
            }
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    func approve(_ request: FollowRequest) async {
        await process(request, method: .post, pathSuffix: "/approve")
    }

    func reject(_ request: FollowRequest) async {
        await process(request, method: .delete, pathSuffix: "")
    }

    private func process(_ request: FollowRequest, method: Endpoint.Method, pathSuffix: String) async {
        errorMessage = nil
        do {
            var endpoint = Endpoint(
                path: "/api/v1/users/me/follow-requests/\(request.followerId)\(pathSuffix)",
                method: method
            )
            if method == .post { endpoint.body = Data("{}".utf8) }
            try await api.sendVoid(endpoint)
            pendingRequests.removeAll { $0.id == request.id }
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}

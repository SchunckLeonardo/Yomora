import Foundation
import Observation

@MainActor
@Observable
final class ProfileViewModel {
    private(set) var profile: UserProfile?
    private(set) var followStatus: FollowStatus?
    private(set) var pendingRequests: [FollowRequest] = []
    private(set) var errorMessage: String?
    private(set) var didSubmitReport = false

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

    func report(reason: String, details: String) async {
        struct Body: Encodable {
            let reportedUserId: UUID
            let postId: UUID?
            let reason: String
            let details: String?
        }
        errorMessage = nil
        didSubmitReport = false
        do {
            var endpoint = Endpoint(path: "/api/v1/moderation/reports", method: .post)
            endpoint.body = try Endpoint.json(Body(
                reportedUserId: userId,
                postId: nil,
                reason: reason,
                details: details.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? nil : details
            ))
            try await api.sendVoid(endpoint)
            didSubmitReport = true
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    func block() async {
        errorMessage = nil
        do {
            var endpoint = Endpoint(path: "/api/v1/moderation/blocks/\(userId)", method: .post)
            endpoint.body = Data("{}".utf8)
            try await api.sendVoid(endpoint)
        } catch {
            errorMessage = error.localizedDescription
        }
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

import SwiftUI

struct ActivityInboxView: View {
    let api: any APIClientProtocol
    @State private var activities: [CommunityActivity] = []
    @State private var actors: [UUID: UserProfile] = [:]
    @State private var posts: [UUID: Post] = [:]
    @State private var loading = true
    @State private var errorMessage: String?

    var body: some View {
        List(activities) { activity in
            activityRow(activity)
                .listRowBackground(activity.read ? YomoraColor.surface : YomoraColor.sereneTeal.opacity(0.10))
                .task { await loadContext(for: activity) }
                .swipeActions {
                    if !activity.read {
                        Button("Marcar como lida") { Task { await markRead(activity) } }
                            .tint(YomoraColor.sereneTeal)
                    }
                }
        }
        .navigationTitle("Atividades")
        .overlay {
            if loading { ProgressView("Carregando atividades…") }
            else if activities.isEmpty {
                EmptyStateView(
                    title: "Tudo tranquilo por aqui",
                    message: "Curtidas, comentários e novos seguidores aparecem nesta caixa.",
                    systemImage: "bell"
                )
            }
        }
        .safeAreaInset(edge: .bottom) {
            if let errorMessage {
                Text(errorMessage)
                    .font(.footnote)
                    .foregroundStyle(YomoraColor.danger)
                    .padding()
            }
        }
        .task { await load() }
    }

    @ViewBuilder
    private func activityRow(_ activity: CommunityActivity) -> some View {
        if let postId = activity.postId, let post = posts[postId] {
            NavigationLink {
                PostDetailsView(post: post, api: api, onPostChange: nil)
            } label: {
                activityLabel(activity)
            }
            .simultaneousGesture(TapGesture().onEnded { Task { await markRead(activity) } })
        } else {
            activityLabel(activity)
                .contentShape(Rectangle())
                .onTapGesture { Task { await markRead(activity) } }
        }
    }

    private func activityLabel(_ activity: CommunityActivity) -> some View {
        HStack(spacing: 12) {
            UserAvatar(url: actors[activity.actorId]?.avatarUrl, size: 44)
            VStack(alignment: .leading, spacing: 4) {
                Text(message(for: activity))
                    .font(.subheadline)
                    .foregroundStyle(YomoraColor.textPrimary)
                Text(activity.createdAt)
                    .font(.caption)
                    .foregroundStyle(YomoraColor.textSecondary)
            }
            Spacer()
            if !activity.read {
                Circle().fill(YomoraColor.sereneTeal).frame(width: 9, height: 9)
                    .accessibilityLabel("Não lida")
            }
        }
        .padding(.vertical, 5)
    }

    private func message(for activity: CommunityActivity) -> String {
        let name = actors[activity.actorId]?.name ?? "Uma pessoa"
        return switch activity.type {
        case .followRequest: "\(name) quer seguir você"
        case .followAccepted: "\(name) aceitou sua solicitação"
        case .userFollowed: "\(name) começou a seguir você"
        case .postLiked: "\(name) curtiu sua publicação"
        case .postCommented: "\(name) comentou na sua publicação"
        }
    }

    private func load() async {
        loading = true
        do {
            activities = try await api.send(
                Endpoint(path: "/api/v1/community/activities"),
                as: [CommunityActivity].self
            )
            errorMessage = nil
        } catch {
            errorMessage = error.localizedDescription
        }
        loading = false
    }

    private func loadContext(for activity: CommunityActivity) async {
        if actors[activity.actorId] == nil {
            actors[activity.actorId] = try? await api.send(
                Endpoint(path: "/api/v1/users/\(activity.actorId)"),
                as: UserProfile.self
            )
        }
        if let postId = activity.postId, posts[postId] == nil {
            posts[postId] = try? await api.send(
                Endpoint(path: "/api/v1/posts/\(postId)"),
                as: Post.self
            )
        }
    }

    private func markRead(_ activity: CommunityActivity) async {
        guard !activity.read else { return }
        do {
            let updated = try await api.send(
                Endpoint(path: "/api/v1/community/activities/\(activity.id)/read", method: .patch),
                as: CommunityActivity.self
            )
            if let index = activities.firstIndex(where: { $0.id == updated.id }) {
                activities[index] = updated
            }
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}

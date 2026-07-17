import Observation
import SwiftUI

enum CommunityActivityDestination: Hashable {
    case profile(UUID)
    case post(Post)
}

@MainActor
@Observable
final class ActivityInboxViewModel {
    private(set) var activities: [CommunityActivity] = []
    private(set) var loading = false
    private(set) var errorMessage: String?
    private let api: any APIClientProtocol

    var unreadCount: Int {
        activities.lazy.filter { !$0.read }.count
    }

    init(api: any APIClientProtocol) {
        self.api = api
    }

    func load() async {
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

    func markRead(_ activity: CommunityActivity) async {
        guard !activity.read else { return }
        do {
            let updated = try await api.send(
                Endpoint(path: "/api/v1/community/activities/\(activity.id)/read", method: .patch),
                as: CommunityActivity.self
            )
            if let index = activities.firstIndex(where: { $0.id == updated.id }) {
                activities[index] = updated
            }
            errorMessage = nil
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}

struct ActivityInboxView: View {
    let api: any APIClientProtocol
    let model: ActivityInboxViewModel
    let onSelect: (CommunityActivityDestination) -> Void
    @State private var actors: [UUID: UserProfile] = [:]
    @State private var posts: [UUID: Post] = [:]
    @State private var contextError: String?

    var body: some View {
        List(model.activities) { activity in
            Button {
                Task { await open(activity) }
            } label: {
                activityLabel(activity)
            }
            .buttonStyle(.plain)
            .accessibilityIdentifier("activityRow_\(activity.id)")
            .listRowBackground(activity.read ? YomoraColor.surface : YomoraColor.sereneTeal.opacity(0.10))
            .task { await loadContext(for: activity) }
            .swipeActions {
                if !activity.read {
                    Button("Marcar como lida") { Task { await model.markRead(activity) } }
                        .tint(YomoraColor.sereneTeal)
                }
            }
        }
        .navigationTitle("Atividades")
        .overlay {
            if model.loading { ProgressView("Carregando atividades…") }
            else if model.activities.isEmpty {
                EmptyStateView(
                    title: "Tudo tranquilo por aqui",
                    message: "Curtidas, comentários e novos seguidores aparecem nesta caixa.",
                    systemImage: "bell"
                )
            }
        }
        .safeAreaInset(edge: .bottom) {
            if let message = contextError ?? model.errorMessage {
                Text(message)
                    .font(.footnote)
                    .foregroundStyle(YomoraColor.danger)
                    .padding()
            }
        }
        .task { await model.load() }
        .refreshable { await model.load() }
    }

    private func activityLabel(_ activity: CommunityActivity) -> some View {
        HStack(spacing: 12) {
            UserAvatar(url: actors[activity.actorId]?.avatarUrl, size: 44)
            VStack(alignment: .leading, spacing: 4) {
                Text(message(for: activity))
                    .font(.subheadline)
                    .foregroundStyle(YomoraColor.textPrimary)
                Text(ActivityDate.display(activity.createdAt))
                    .font(.caption)
                    .foregroundStyle(YomoraColor.textSecondary)
                    .accessibilityIdentifier("activityDate_\(activity.id)")
            }
            Spacer()
            if !activity.read {
                Circle().fill(YomoraColor.sereneTeal).frame(width: 9, height: 9)
                    .accessibilityLabel("Não lida")
            }
            Image(systemName: "chevron.right")
                .font(.caption.weight(.semibold))
                .foregroundStyle(YomoraColor.textSecondary)
        }
        .padding(.vertical, 5)
        .contentShape(Rectangle())
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

    private func open(_ activity: CommunityActivity) async {
        await model.markRead(activity)
        if let postId = activity.postId {
            if posts[postId] == nil {
                posts[postId] = try? await api.send(
                    Endpoint(path: "/api/v1/posts/\(postId)"),
                    as: Post.self
                )
            }
            guard let post = posts[postId] else {
                contextError = "Não foi possível abrir a publicação desta atividade."
                return
            }
            contextError = nil
            onSelect(.post(post))
        } else {
            contextError = nil
            onSelect(.profile(activity.actorId))
        }
    }
}

enum ActivityDate {
    static func display(
        _ value: String,
        relativeTo now: Date = .now,
        timeZone: TimeZone = .current
    ) -> String {
        guard let date = parse(value) else { return value }
        let elapsed = max(0, Int(now.timeIntervalSince(date)))
        switch elapsed {
        case 0..<60:
            return "agora"
        case 60..<3_600:
            return "há \(elapsed / 60) min"
        case 3_600..<86_400:
            return "há \(elapsed / 3_600) h"
        case 86_400..<604_800:
            return "há \(elapsed / 86_400) d"
        default:
            let formatter = DateFormatter()
            formatter.locale = Locale(identifier: "pt_BR")
            formatter.timeZone = timeZone
            formatter.dateFormat = "d 'de' MMM, HH:mm"
            return formatter.string(from: date)
        }
    }

    private static func parse(_ value: String) -> Date? {
        let fractional = ISO8601DateFormatter()
        fractional.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        return fractional.date(from: value) ?? ISO8601DateFormatter().date(from: value)
    }
}

import SwiftUI

struct ReadingGoalCard: View {
    let minutes: Int
    let completed: Int
    var body: some View {
        HStack(spacing: YomoraSpacing.md) {
            ProgressRing(progress: minutes == 0 ? 0 : Double(completed) / Double(minutes), size: 76)
            VStack(alignment: .leading, spacing: 6) {
                Text("Meta de hoje").font(.yomoraHeading)
                Text("\(completed) de \(minutes) minutos")
                Text(completed >= minutes ? "Um passo sereno por vez." : "Sua próxima página está esperando.")
                    .font(.caption).foregroundStyle(.secondary)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(YomoraSpacing.md)
        .background(YomoraColor.surface, in: RoundedRectangle(cornerRadius: YomoraRadius.card))
        .overlay(RoundedRectangle(cornerRadius: YomoraRadius.card).stroke(YomoraColor.outline.opacity(0.55)))
    }
}

struct UserAvatar: View {
    let url: String?
    var size: CGFloat = 44
    var body: some View {
        AsyncImage(url: url.flatMap(URL.init(string:))) { phase in
            if case let .success(image) = phase { image.resizable().scaledToFill() }
            else { Image(systemName: "person.crop.circle.fill").resizable().foregroundStyle(YomoraColor.sereneTeal) }
        }
        .frame(width: size, height: size).clipShape(Circle())
        .accessibilityLabel("Foto do perfil")
    }
}

struct PostCard: View {
    let post: Post
    var api: (any APIClientProtocol)?
    var isLiked = false
    var onOpen: (() -> Void)?
    var onOpenProfile: (() -> Void)?
    var onLike: (() -> Void)?
    var onComment: (() -> Void)?
    var onReport: (() -> Void)?

    @State private var author: UserProfile?
    @State private var spoilerRevealed = false

    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        VStack(alignment: .leading, spacing: YomoraSpacing.md) {
            authorHeader
            postContent

            HStack(spacing: YomoraSpacing.sm) {
                if let onLike {
                    Button(action: onLike) {
                        Label("\(post.likeCount)", systemImage: isLiked ? "heart.fill" : "heart")
                            .contentTransition(.numericText())
                            .symbolEffect(.bounce, value: isLiked)
                    }
                    .foregroundStyle(isLiked ? YomoraColor.sereneTeal : YomoraColor.textSecondary)
                    .background(
                        isLiked ? YomoraColor.sereneTeal.opacity(colorScheme == .dark ? 0.16 : 0.10) : YomoraColor.surfaceElevated,
                        in: Capsule()
                    )
                    .accessibilityLabel(isLiked ? "Descurtir" : "Curtir")
                    .accessibilityValue("\(post.likeCount) curtidas")
                    .accessibilityIdentifier("postLike_\(post.id)")
                } else {
                    interactionLabel("\(post.likeCount)", systemImage: isLiked ? "heart.fill" : "heart")
                }

                if let onComment {
                    Button(action: onComment) { Label("\(post.commentCount)", systemImage: "bubble.left") }
                        .accessibilityLabel("Abrir comentários")
                        .accessibilityValue("\(post.commentCount) comentários")
                } else {
                    interactionLabel("\(post.commentCount)", systemImage: "bubble.left")
                }

                Spacer(minLength: 0)
                ShareLink(item: URL(string: "https://yomora.app/posts/\(post.id)")!) {
                    Label("Compartilhar", systemImage: "square.and.arrow.up").labelStyle(.iconOnly)
                }
                .accessibilityLabel("Compartilhar publicação")
            }
            .font(.subheadline.weight(.semibold))
            .foregroundStyle(YomoraColor.textSecondary)
            .buttonStyle(SocialActionButtonStyle())
        }
        .padding(YomoraSpacing.md)
        .background(YomoraColor.surface, in: RoundedRectangle(cornerRadius: YomoraRadius.card))
        .overlay(RoundedRectangle(cornerRadius: YomoraRadius.card).stroke(YomoraColor.outline.opacity(0.58)))
        .shadow(color: .black.opacity(colorScheme == .dark ? 0.26 : 0.06), radius: 12, y: 5)
        .task(id: post.authorId) {
            guard author == nil, let api else { return }
            author = try? await api.send(
                Endpoint(path: "/api/v1/users/\(post.authorId)"),
                as: UserProfile.self
            )
        }
    }

    private var authorHeader: some View {
        HStack(spacing: 11) {
            authorProfileControl
            Spacer(minLength: 8)
            VStack(alignment: .trailing, spacing: 5) {
                Text("\(post.type.localizedTitle) · \(SocialDate.relative(post.createdAt))")
                    .font(.caption)
                    .foregroundStyle(YomoraColor.textSecondary)
                if post.spoiler {
                    Label("Spoiler", systemImage: "eye.slash")
                        .font(.caption.weight(.semibold))
                        .foregroundStyle(YomoraColor.progressGold)
                        .padding(.horizontal, 9).padding(.vertical, 6)
                        .background(YomoraColor.progressGold.opacity(0.12), in: Capsule())
                }
            }
            if let onReport {
                Menu {
                    Button(role: .destructive, action: onReport) {
                        Label("Denunciar publicação", systemImage: "exclamationmark.bubble")
                    }
                } label: {
                    Image(systemName: "ellipsis")
                        .frame(width: 32, height: 32)
                }
                .accessibilityLabel("Opções da publicação")
            }
        }
    }

    @ViewBuilder
    private var authorProfileControl: some View {
        if let onOpenProfile {
            Button(action: onOpenProfile) { authorIdentity }
                .buttonStyle(.plain)
                .accessibilityIdentifier("postAuthorProfileButton_\(post.id)")
        } else {
            NavigationLink(value: AppRoute.profile(post.authorId)) {
                authorIdentity
            }
            .buttonStyle(.plain)
            .accessibilityIdentifier("postAuthorProfileButton_\(post.id)")
        }
    }

    private var authorIdentity: some View {
        HStack(spacing: 11) {
            UserAvatar(url: author?.avatarUrl)
            VStack(alignment: .leading, spacing: 3) {
                Text(author?.name ?? "Leitor Yomora")
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(YomoraColor.textPrimary)
                Text("@\(author?.username ?? "leitor")")
                    .font(.caption)
                    .foregroundStyle(YomoraColor.textSecondary)
            }
        }
    }

    @ViewBuilder
    private var postContent: some View {
        if post.spoiler && !spoilerRevealed {
            Button {
                withAnimation(.easeInOut(duration: 0.2)) { spoilerRevealed = true }
            } label: {
                VStack(alignment: .leading, spacing: 3) {
                    Label("Conteúdo com spoiler", systemImage: "eye.slash.fill")
                        .font(.headline)
                    Text("Toque para mostrar esta publicação.")
                        .font(.subheadline)
                }
                .foregroundStyle(YomoraColor.textSecondary)
                .frame(maxWidth: .infinity, minHeight: 96)
                .background(YomoraColor.surfaceElevated, in: RoundedRectangle(cornerRadius: 14))
            }
            .buttonStyle(.plain)
            .accessibilityLabel("Mostrar spoiler")
        } else if let onOpen {
            Button(action: onOpen) { postText }
                .buttonStyle(.plain)
                .accessibilityHint("Abre a publicação e a conversa")
        } else {
            postText
        }
        if post.editionId != nil {
            Label("Publicação vinculada a um livro", systemImage: "book.closed")
                .font(.caption.weight(.semibold))
                .foregroundStyle(YomoraColor.sereneTeal)
        }
    }

    private var postText: some View {
        Text(post.text)
            .font(.yomoraEditorial)
            .foregroundStyle(YomoraColor.textPrimary)
            .multilineTextAlignment(.leading)
            .lineSpacing(4)
            .frame(maxWidth: .infinity, alignment: .leading)
            .contentShape(Rectangle())
    }

    private func interactionLabel(_ title: String, systemImage: String) -> some View {
        Label(title, systemImage: systemImage)
            .frame(minHeight: 44)
            .padding(.horizontal, 12)
            .background(YomoraColor.surfaceElevated, in: Capsule())
    }
}

struct CommentCard: View {
    let comment: Comment
    var api: (any APIClientProtocol)?
    var highlighted = false
    let onOpenProfile: () -> Void

    @State private var author: UserProfile?

    var body: some View {
        HStack(alignment: .top, spacing: 11) {
            Button(action: onOpenProfile) {
                UserAvatar(url: author?.avatarUrl, size: 36)
            }
            .buttonStyle(.plain)
            .accessibilityIdentifier("commentAuthorProfileButton_\(comment.id)")
            VStack(alignment: .leading, spacing: 6) {
                HStack(alignment: .firstTextBaseline) {
                    Button(action: onOpenProfile) {
                        VStack(alignment: .leading, spacing: 2) {
                            Text(author?.name ?? "Leitor Yomora").font(.subheadline.weight(.semibold))
                            Text("@\(author?.username ?? "leitor")")
                                .font(.caption)
                                .foregroundStyle(YomoraColor.textSecondary)
                        }
                    }
                    .buttonStyle(.plain)
                    Spacer()
                    Text(SocialDate.relative(comment.createdAt))
                        .font(.caption)
                        .foregroundStyle(YomoraColor.textSecondary)
                }
                Text(comment.text)
                    .font(.yomoraEditorial)
                    .foregroundStyle(YomoraColor.textPrimary)
                    .lineSpacing(3)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
        .padding(14)
        .background(
            highlighted ? YomoraColor.sereneTeal.opacity(0.12) : YomoraColor.surfaceElevated,
            in: RoundedRectangle(cornerRadius: 16)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(highlighted ? YomoraColor.sereneTeal.opacity(0.55) : YomoraColor.outline.opacity(0.35))
        )
        .accessibilityElement(children: .contain)
        .task(id: comment.authorId) {
            guard author == nil, let api else { return }
            author = try? await api.send(
                Endpoint(path: "/api/v1/users/\(comment.authorId)"),
                as: UserProfile.self
            )
        }
    }
}

private struct SocialActionButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .frame(minHeight: 44)
            .padding(.horizontal, 12)
            .scaleEffect(configuration.isPressed ? 0.96 : 1)
            .opacity(configuration.isPressed ? 0.82 : 1)
            .animation(.easeOut(duration: 0.15), value: configuration.isPressed)
    }
}

private enum SocialDate {
    static func relative(_ value: String) -> String {
        let iso = ISO8601DateFormatter()
        guard let date = iso.date(from: value) else { return "agora" }
        let formatter = RelativeDateTimeFormatter()
        formatter.locale = Locale(identifier: "pt_BR")
        formatter.unitsStyle = .short
        return formatter.localizedString(for: date, relativeTo: .now)
    }
}

private extension PostType {
    var localizedTitle: String {
        switch self {
        case .note: "Nota de leitura"
        case .review: "Review"
        case .recommendation: "Recomendação"
        case .progress: "Progresso"
        case .quote: "Citação"
        }
    }
}

struct EmptyStateView: View {
    let title: String
    let message: String
    var systemImage = "books.vertical"
    var body: some View {
        ContentUnavailableView(title, systemImage: systemImage, description: Text(message))
            .accessibilityElement(children: .combine)
    }
}

struct ErrorStateView: View {
    let message: String
    let retry: () -> Void
    var body: some View {
        ContentUnavailableView {
            Label("Algo saiu do ritmo", systemImage: "exclamationmark.triangle")
        } description: { Text(message) } actions: { Button("Tentar novamente", action: retry).buttonStyle(.borderedProminent) }
    }
}

struct LoadingSkeleton: View {
    var body: some View {
        RoundedRectangle(cornerRadius: YomoraRadius.card)
            .fill(.secondary.opacity(0.13)).frame(height: 136)
            .overlay(ProgressView())
            .accessibilityLabel("Carregando")
    }
}

struct ThemePicker: View {
    @Binding var theme: AppTheme
    @Binding var accent: AccentChoice
    var body: some View {
        Form {
            Section("Aparência") {
                Picker("Tema", selection: $theme) {
                    ForEach(AppTheme.allCases) { Text($0.title).tag($0) }
                }
            }
            Section("Cor de destaque") {
                ForEach(AccentChoice.allCases) { choice in
                    Button { accent = choice } label: {
                        HStack { Circle().fill(choice.color).frame(width: 24, height: 24); Text(choice.title); Spacer(); if accent == choice { Image(systemName: "checkmark") } }
                    }.foregroundStyle(.primary)
                }
            }
        }
    }
}

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
        .background(.background, in: RoundedRectangle(cornerRadius: YomoraRadius.card))
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
    var onLike: () -> Void = { }
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                UserAvatar(url: nil)
                VStack(alignment: .leading) {
                    Text("Leitor Yomora").font(.headline)
                    Text(post.type.rawValue.capitalized).font(.caption).foregroundStyle(.secondary)
                }
                Spacer()
                if post.spoiler { Label("Spoiler", systemImage: "eye.slash").font(.caption) }
            }
            Text(post.text).font(.yomoraEditorial)
            HStack(spacing: 24) {
                Button(action: onLike) { Label("\(post.likeCount)", systemImage: "heart") }
                Label("\(post.commentCount)", systemImage: "bubble.left")
                Spacer()
                ShareLink(item: URL(string: "https://yomora.app/posts/\(post.id)")!) {
                    Image(systemName: "square.and.arrow.up")
                }
            }
            .font(.subheadline).foregroundStyle(YomoraColor.sereneTeal)
        }
        .padding(YomoraSpacing.md)
        .background(.background, in: RoundedRectangle(cornerRadius: YomoraRadius.card))
        .shadow(color: .black.opacity(0.05), radius: 8, y: 3)
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


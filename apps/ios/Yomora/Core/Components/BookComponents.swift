import SwiftUI

struct BookCover: View {
    let url: String?
    var width: CGFloat = 76
    var height: CGFloat = 112

    var body: some View {
        AsyncImage(url: url.flatMap(URL.init(string:))) { phase in
            if case let .success(image) = phase { image.resizable().scaledToFill() }
            else {
                ZStack {
                    LinearGradient(colors: [YomoraColor.sereneTeal, YomoraColor.primary], startPoint: .top, endPoint: .bottom)
                    Image(systemName: "book.closed.fill").foregroundStyle(.white.opacity(0.9))
                }
            }
        }
        .frame(width: width, height: height)
        .clipShape(RoundedRectangle(cornerRadius: YomoraRadius.cover))
        .shadow(color: .black.opacity(0.15), radius: 6, y: 3)
        .accessibilityLabel("Capa do livro")
    }
}

struct BookCard: View {
    let book: Book
    var progress: Double?

    var body: some View {
        HStack(alignment: .top, spacing: YomoraSpacing.md) {
            BookCover(url: book.coverUrl)
            VStack(alignment: .leading, spacing: YomoraSpacing.sm) {
                Text(book.title).font(.yomoraHeading).lineLimit(2)
                Text(book.authorLine).font(.subheadline).foregroundStyle(.secondary).lineLimit(1)
                if let progress {
                    ProgressView(value: progress).tint(YomoraColor.progressGold)
                    Text("\(Int(progress * 100))% concluído").font(.caption).foregroundStyle(.secondary)
                } else if let pages = book.pageCount {
                    Label("\(pages) páginas", systemImage: "book.pages").font(.caption).foregroundStyle(.secondary)
                }
            }
            Spacer(minLength: 0)
        }
        .padding(YomoraSpacing.md)
        .background(YomoraColor.surface, in: RoundedRectangle(cornerRadius: YomoraRadius.card))
        .overlay(RoundedRectangle(cornerRadius: YomoraRadius.card).stroke(YomoraColor.outline.opacity(0.5)))
        .shadow(color: .black.opacity(0.06), radius: 10, y: 4)
        .accessibilityElement(children: .combine)
    }
}

struct ProgressRing: View {
    let progress: Double
    var size: CGFloat = 92
    var body: some View {
        ZStack {
            Circle().stroke(YomoraColor.sereneTeal.opacity(0.15), lineWidth: 10)
            Circle().trim(from: 0, to: min(max(progress, 0), 1))
                .stroke(YomoraColor.progressGold, style: StrokeStyle(lineWidth: 10, lineCap: .round))
                .rotationEffect(.degrees(-90))
            Text("\(Int(progress * 100))%")
                .font(.headline.monospacedDigit())
        }
        .frame(width: size, height: size)
        .accessibilityLabel("Progresso")
        .accessibilityValue("\(Int(progress * 100)) por cento")
    }
}

struct RatingView: View {
    @Binding var rating: Int
    var interactive = true
    var body: some View {
        HStack(spacing: 6) {
            ForEach(1...5, id: \.self) { value in
                Button { if interactive { rating = value } } label: {
                    Image(systemName: value <= rating ? "star.fill" : "star")
                        .foregroundStyle(YomoraColor.progressGold)
                        .frame(width: 32, height: 44)
                }
                .buttonStyle(.plain)
                .disabled(!interactive)
                .accessibilityLabel("\(value) estrelas")
            }
        }
    }
}

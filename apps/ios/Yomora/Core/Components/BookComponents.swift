import CryptoKit
import Foundation
import SwiftUI
import UIKit

actor BookCoverImageCache {
    typealias Loader = @Sendable (URL) async throws -> Data

    static let shared = BookCoverImageCache()

    private let directory: URL
    private let loader: Loader
    private let memory = NSCache<NSURL, NSData>()

    init(directory: URL? = nil, loader: @escaping Loader = BookCoverImageCache.download) {
        self.directory = directory ?? Self.defaultDirectory()
        self.loader = loader
        try? FileManager.default.createDirectory(
            at: self.directory,
            withIntermediateDirectories: true
        )
    }

    func data(for url: URL) async throws -> Data {
        if let cached = memory.object(forKey: url as NSURL) {
            return cached as Data
        }

        let file = directory.appendingPathComponent(cacheKey(for: url), isDirectory: false)
        if let cached = try? Data(contentsOf: file), !cached.isEmpty {
            memory.setObject(cached as NSData, forKey: url as NSURL)
            return cached
        }

        let downloaded = try await loader(url)
        try? downloaded.write(to: file, options: .atomic)
        memory.setObject(downloaded as NSData, forKey: url as NSURL)
        return downloaded
    }

    private func cacheKey(for url: URL) -> String {
        SHA256.hash(data: Data(url.absoluteString.utf8))
            .map { String(format: "%02x", $0) }
            .joined()
    }

    private static func defaultDirectory() -> URL {
        let caches = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first
            ?? FileManager.default.temporaryDirectory
        return caches.appendingPathComponent("YomoraBookCovers", isDirectory: true)
    }

    private static func download(_ url: URL) async throws -> Data {
        let (data, response) = try await URLSession.shared.data(from: url)
        guard let http = response as? HTTPURLResponse,
              (200..<300).contains(http.statusCode),
              !data.isEmpty else {
            throw URLError(.badServerResponse)
        }
        return data
    }
}

struct BookCover: View {
    let url: String?
    var width: CGFloat = 76
    var height: CGFloat = 112
    @State private var imageData: Data?

    var body: some View {
        Group {
            if let imageData, let image = UIImage(data: imageData) {
                Image(uiImage: image).resizable().scaledToFill()
            } else {
                placeholder
            }
        }
        .frame(width: width, height: height)
        .clipShape(RoundedRectangle(cornerRadius: YomoraRadius.cover))
        .shadow(color: .black.opacity(0.15), radius: 6, y: 3)
        .accessibilityLabel("Capa do livro")
        .task(id: url) {
            imageData = nil
            guard let url = url.flatMap(URL.init(string:)) else {
                return
            }
            imageData = try? await BookCoverImageCache.shared.data(for: url)
        }
    }

    private var placeholder: some View {
        ZStack {
            LinearGradient(
                colors: [YomoraColor.sereneTeal, YomoraColor.primary],
                startPoint: .top,
                endPoint: .bottom
            )
            Image(systemName: "book.closed.fill").foregroundStyle(.white.opacity(0.9))
        }
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

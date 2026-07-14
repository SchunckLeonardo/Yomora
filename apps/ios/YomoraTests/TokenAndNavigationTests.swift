import SwiftUI
import UIKit
import XCTest
@testable import Yomora

final class TokenAndNavigationTests: XCTestCase {
    func testInMemoryTokenStoreRoundTripAndClear() async throws {
        let store = InMemoryTokenStore()
        let pair = TokenPair(accessToken: "a", refreshToken: "r", tokenType: "Bearer", expiresIn: 60)
        await store.save(pair)
        let stored = await store.load()
        XCTAssertEqual(stored, pair)
        await store.clear()
        let cleared = await store.load()
        XCTAssertNil(cleared)
    }

    func testTypedRoutesAreHashable() {
        let id = UUID()
        XCTAssertEqual(Set([AppRoute.profile(id), AppRoute.profile(id)]).count, 1)
    }
}

final class YomoraDesignTests: XCTestCase {
    func testDarkPaletteKeepsReadingTextAtAAContrast() {
        let canvas = resolvedRGB(YomoraColor.canvas, style: .dark)
        let surface = resolvedRGB(YomoraColor.surface, style: .dark)

        XCTAssertGreaterThanOrEqual(contrast(canvas, resolvedRGB(YomoraColor.textPrimary, style: .dark)), 4.5)
        XCTAssertGreaterThanOrEqual(contrast(canvas, resolvedRGB(YomoraColor.textSecondary, style: .dark)), 4.5)
        XCTAssertGreaterThanOrEqual(contrast(surface, resolvedRGB(YomoraColor.textPrimary, style: .dark)), 4.5)
    }

    func testDarkCommunityAccentIsReadableOnElevatedSurface() {
        let surface = resolvedRGB(YomoraColor.surfaceElevated, style: .dark)
        let accent = resolvedRGB(YomoraColor.sereneTeal, style: .dark)

        XCTAssertGreaterThanOrEqual(contrast(surface, accent), 4.5)
    }

    private func resolvedRGB(_ color: Color, style: UIUserInterfaceStyle) -> RGB {
        let trait = UITraitCollection(userInterfaceStyle: style)
        let resolved = UIColor(color).resolvedColor(with: trait)
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        XCTAssertTrue(resolved.getRed(&red, green: &green, blue: &blue, alpha: &alpha))
        return RGB(red: Double(red), green: Double(green), blue: Double(blue))
    }

    private func contrast(_ lhs: RGB, _ rhs: RGB) -> Double {
        let lighter = max(lhs.luminance, rhs.luminance)
        let darker = min(lhs.luminance, rhs.luminance)
        return (lighter + 0.05) / (darker + 0.05)
    }

    private struct RGB {
        let red: Double
        let green: Double
        let blue: Double

        var luminance: Double {
            0.2126 * linear(red) + 0.7152 * linear(green) + 0.0722 * linear(blue)
        }

        private func linear(_ component: Double) -> Double {
            component <= 0.04045 ? component / 12.92 : pow((component + 0.055) / 1.055, 2.4)
        }
    }
}

final class KeyboardLayoutTests: XCTestCase {
    func testBottomInsetReservesOnlyKeyboardAreaAboveTheHomeIndicator() {
        XCTAssertEqual(
            KeyboardLayout.bottomInset(screenHeight: 852, keyboardMinY: 518, bottomSafeArea: 34),
            300
        )
    }

    func testBottomInsetIsZeroWhenKeyboardIsHidden() {
        XCTAssertEqual(
            KeyboardLayout.bottomInset(screenHeight: 852, keyboardMinY: 852, bottomSafeArea: 34),
            0
        )
    }
}

final class AppConfigurationTests: XCTestCase {
    func testApplicationBundleContainsAValidAPIBaseURL() throws {
        let configured = try XCTUnwrap(Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String)
        let url = try XCTUnwrap(URL(string: configured))

        XCTAssertNotNil(url.scheme)
        XCTAssertNotNil(url.host)
    }
}

@MainActor
final class FeedViewModelTests: XCTestCase {
    func testLikeActionTogglesVisualStateAndUsesMatchingEndpointMethods() async {
        let post = makePost(likeCount: 0)
        let api = FeedEngagementAPI(post: post)
        let viewModel = FeedViewModel(api: api)
        await viewModel.load()

        XCTAssertFalse(viewModel.isLiked(post))

        await viewModel.toggleLike(post)

        XCTAssertTrue(viewModel.isLiked(post))
        XCTAssertEqual(viewModel.posts.first?.likeCount, 1)

        await viewModel.toggleLike(viewModel.posts[0])

        XCTAssertFalse(viewModel.isLiked(post))
        XCTAssertEqual(viewModel.posts.first?.likeCount, 0)
        let methods = await api.methods()
        XCTAssertEqual(methods, [.get, .post, .delete])
    }

    private func makePost(likeCount: Int) -> Post {
        Post(
            id: UUID(),
            authorId: UUID(),
            text: "Uma leitura que permanece depois da última página.",
            editionId: nil,
            type: .recommendation,
            spoiler: false,
            spoilerPage: nil,
            visibility: .publicPost,
            createdAt: "2026-07-14T10:00:00Z",
            updatedAt: "2026-07-14T10:00:00Z",
            likeCount: likeCount,
            commentCount: 2
        )
    }
}

private actor FeedEngagementAPI: APIClientProtocol {
    private var post: Post
    private var endpoints: [Endpoint] = []

    init(post: Post) { self.post = post }

    func send<T: Decodable & Sendable>(_ endpoint: Endpoint, as type: T.Type) async throws -> T {
        endpoints.append(endpoint)
        let data: Data
        if endpoint.path.hasSuffix("/likes") {
            let nextCount = endpoint.method == .post ? post.likeCount + 1 : max(0, post.likeCount - 1)
            post = Post(
                id: post.id,
                authorId: post.authorId,
                text: post.text,
                editionId: post.editionId,
                type: post.type,
                spoiler: post.spoiler,
                spoilerPage: post.spoilerPage,
                visibility: post.visibility,
                createdAt: post.createdAt,
                updatedAt: post.updatedAt,
                likeCount: nextCount,
                commentCount: post.commentCount
            )
            data = post.encoded
        } else {
            data = [post].encoded
        }
        return try JSONDecoder().decode(T.self, from: data)
    }

    func sendVoid(_ endpoint: Endpoint) async throws { endpoints.append(endpoint) }

    func methods() -> [Endpoint.Method] { endpoints.map(\.method) }
}

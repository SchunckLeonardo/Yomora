import Foundation
import Observation
import SwiftUI

extension Notification.Name {
    static let libraryDidChange = Notification.Name("YomoraLibraryDidChange")
}

private enum MainTab: Hashable {
    case today, discover, read, community, library
}

struct MainTabView: View {
    let container: AppContainer
    let session: SessionStore
    @Environment(\.scenePhase) private var scenePhase
    @State private var selectedTab: MainTab = .today
    @State private var activeSessionRouteRequest = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            FeatureNavigation(container: container, session: session, onExitToToday: selectToday, activeSessionRouteRequest: 0) {
                TodayView(container: container, user: session.user)
            }
                .tabItem { Label("Hoje", systemImage: "sun.max") }
                .tag(MainTab.today)
            FeatureNavigation(container: container, session: session, onExitToToday: selectToday, activeSessionRouteRequest: 0) {
                SearchView(container: container)
            }
                .tabItem { Label("Descobrir", systemImage: "magnifyingglass") }
                .tag(MainTab.discover)
            FeatureNavigation(
                container: container,
                session: session,
                onExitToToday: selectToday,
                activeSessionRouteRequest: activeSessionRouteRequest
            ) {
                QuickReadView(container: container)
            }
                .tabItem { Label("Ler", systemImage: "timer") }
                .tag(MainTab.read)
            FeatureNavigation(container: container, session: session, onExitToToday: selectToday, activeSessionRouteRequest: 0) {
                FeedView(container: container)
            }
                .tabItem { Label("Comunidade", systemImage: "person.2") }
                .tag(MainTab.community)
            FeatureNavigation(container: container, session: session, onExitToToday: selectToday, activeSessionRouteRequest: 0) {
                LibraryView(container: container)
            }
                .tabItem { Label("Biblioteca", systemImage: "books.vertical") }
                .tag(MainTab.library)
        }
        .task { await container.activeReadingSession.restore() }
        .task(id: container.navigation.requestId) { await handleNavigationRequest() }
        .onChange(of: scenePhase) { _, phase in
            guard phase == .active else { return }
            Task { await container.activeReadingSession.restore() }
        }
        .onDisappear {
            container.activeReadingSession.clear()
            Task { await container.readingActivity.end() }
        }
    }

    private func selectToday() {
        selectedTab = .today
    }

    private func handleNavigationRequest() async {
        let requestId = container.navigation.requestId
        guard let destination = container.navigation.destination else { return }
        switch destination {
        case .today:
            selectedTab = .today
        case .activeReadingSession:
            selectedTab = .read
            await container.activeReadingSession.restore()
            if container.activeReadingSession.context != nil {
                activeSessionRouteRequest += 1
            }
        }
        container.navigation.consume(requestId)
    }
}

private struct FeatureNavigation<Content: View>: View {
    let container: AppContainer
    let session: SessionStore
    let onExitToToday: () -> Void
    let activeSessionRouteRequest: Int
    @ViewBuilder let content: () -> Content
    @State private var path: [AppRoute] = []

    var body: some View {
        NavigationStack(path: $path) {
            content()
                .navigationDestination(for: AppRoute.self) { route in
                    switch route {
                    case let .book(book): BookDetailsView(book: book, api: container.api)
                    case let .libraryBook(entry): LibraryBookDetailsView(entry: entry, container: container)
                    case let .reading(entry, book):
                        let active = container.activeReadingSession.context
                        let matchingActive = active?.entry.id == entry.id ? active : nil
                        ReadingSessionView(
                            entry: entry,
                            book: book ?? matchingActive?.book,
                            existingSession: matchingActive?.session,
                            container: container,
                            onExitToToday: finishReadingFlow
                        )
                    case let .sessionSummary(summary):
                        SessionSummaryView(
                            summary: summary,
                            title: "Sua leitura",
                            onExitToToday: finishReadingFlow
                        )
                    case let .post(post): PostDetailsView(post: post, api: container.api)
                    case .composer: PostComposerView(api: container.api)
                    case let .profile(id): ProfileView(userId: id, container: container, session: session)
                    case let .followers(id): FollowersView(userId: id, api: container.api)
                    case .theme: SettingsView(container: container, session: session)
                    }
                }
        }
        .task(id: activeSessionRouteRequest) {
            guard activeSessionRouteRequest > 0,
                  let context = container.activeReadingSession.context else { return }
            let route = AppRoute.reading(context.entry, book: context.book)
            if path.last != route { path.append(route) }
        }
    }

    private func finishReadingFlow() {
        path.removeAll()
        onExitToToday()
    }
}

@MainActor
@Observable
final class QuickReadViewModel {
    enum State: Equatable { case idle, loading, loaded, error(String) }

    private(set) var state: State = .idle
    private(set) var items: [LibraryItem] = []
    private(set) var selectedEntryID: UUID?
    private let api: any APIClientProtocol

    var selectedItem: LibraryItem? {
        guard let selectedEntryID else { return items.first }
        return items.first(where: { $0.entry.id == selectedEntryID })
    }

    init(api: any APIClientProtocol) {
        self.api = api
    }

    func load() async {
        let previousState = state
        if previousState != .loaded { state = .loading }
        do {
            let endpoint = Endpoint(
                path: "/api/v1/library",
                query: [URLQueryItem(name: "status", value: ReadingStatus.reading.rawValue)]
            )
            let entries = try await api.send(endpoint, as: [LibraryBook].self)
                .filter { $0.status == .reading }
            var loaded: [LibraryItem] = []
            for entry in entries {
                if let book = try? await api.send(
                    Endpoint(path: "/api/v1/books/\(entry.editionId)", authenticated: false),
                    as: Book.self
                ) {
                    loaded.append(LibraryItem(entry: entry, book: book))
                }
            }
            items = loaded
            if !loaded.contains(where: { $0.entry.id == selectedEntryID }) {
                selectedEntryID = loaded.first?.entry.id
            }
            state = .loaded
        } catch {
            state = .error(error.localizedDescription)
        }
    }

    func select(_ entryID: UUID) {
        guard items.contains(where: { $0.entry.id == entryID }) else { return }
        selectedEntryID = entryID
    }
}

private struct QuickReadView: View {
    let container: AppContainer
    @State private var viewModel: QuickReadViewModel

    init(container: AppContainer) {
        self.container = container
        _viewModel = State(initialValue: QuickReadViewModel(api: container.api))
    }

    var body: some View {
        VStack(spacing: 0) {
            if let active = container.activeReadingSession.context {
                ActiveReadingSessionBanner(context: active)
                    .padding([.horizontal, .top])
            }
            Group {
                switch viewModel.state {
                case .idle, .loading:
                    VStack(spacing: YomoraSpacing.md) {
                        LoadingSkeleton()
                        LoadingSkeleton()
                    }
                    .padding()
                case let .error(message):
                    ErrorStateView(message: message) { Task { await viewModel.load() } }
                case .loaded:
                    content
                }
            }
        }
        .navigationTitle("Ler")
        .task { if viewModel.state == .idle { await viewModel.load() } }
        .refreshable { await viewModel.load() }
        .onReceive(NotificationCenter.default.publisher(for: .libraryDidChange)) { _ in
            Task { await viewModel.load() }
        }
    }

    @ViewBuilder
    private var content: some View {
        if let selected = viewModel.selectedItem {
            ScrollView {
                VStack(spacing: 24) {
                    if viewModel.items.count > 1 {
                        VStack(alignment: .leading, spacing: YomoraSpacing.sm) {
                            Text("Escolha sua leitura").font(.headline)
                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: YomoraSpacing.md) {
                                    ForEach(viewModel.items) { item in
                                        Button { viewModel.select(item.entry.id) } label: {
                                            VStack(alignment: .leading, spacing: 6) {
                                                BookCover(url: item.book.coverUrl, width: 70, height: 102)
                                                Text(item.book.title)
                                                    .font(.caption.weight(.semibold))
                                                    .lineLimit(2)
                                                    .frame(width: 92, alignment: .leading)
                                            }
                                            .padding(8)
                                            .background(YomoraColor.surface, in: RoundedRectangle(cornerRadius: YomoraRadius.card))
                                            .overlay {
                                                RoundedRectangle(cornerRadius: YomoraRadius.card)
                                                    .stroke(
                                                        item.entry.id == selected.entry.id ? YomoraColor.sereneTeal : YomoraColor.outline.opacity(0.5),
                                                        lineWidth: item.entry.id == selected.entry.id ? 2 : 1
                                                    )
                                            }
                                        }
                                        .buttonStyle(.plain)
                                        .accessibilityLabel("Selecionar \(item.book.title)")
                                        .accessibilityIdentifier("quickReadBook-\(item.entry.id)")
                                    }
                                }
                            }
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                    }

                    BookCover(url: selected.book.coverUrl, width: 150, height: 220)
                    VStack {
                        Text("Pronto para mais um capítulo?")
                            .font(.yomoraTitle)
                            .multilineTextAlignment(.center)
                        Text(selected.book.title).foregroundStyle(.secondary)
                    }
                    NavigationLink(value: AppRoute.reading(selected.entry, book: selected.book)) {
                        Label("Iniciar leitura", systemImage: "play.fill")
                            .font(.headline)
                            .frame(maxWidth: .infinity, minHeight: 54)
                            .foregroundStyle(YomoraColor.onInteractive)
                            .background(YomoraColor.interactiveFill, in: RoundedRectangle(cornerRadius: 14))
                    }
                    .accessibilityIdentifier("quickReadButton")
                }
                .padding()
            }
        } else {
            EmptyStateView(
                title: "Nenhum livro em andamento",
                message: "Mude um livro para Lendo e ele aparecerá aqui."
            )
        }
    }
}

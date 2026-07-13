import SwiftUI

struct MainTabView: View {
    let container: AppContainer
    let session: SessionStore

    var body: some View {
        TabView {
            FeatureNavigation(container: container, session: session) { TodayView(container: container, user: session.user) }
                .tabItem { Label("Hoje", systemImage: "sun.max") }
            FeatureNavigation(container: container, session: session) { SearchView(container: container) }
                .tabItem { Label("Descobrir", systemImage: "magnifyingglass") }
            FeatureNavigation(container: container, session: session) { QuickReadView(container: container) }
                .tabItem { Label("Ler", systemImage: "timer") }
            FeatureNavigation(container: container, session: session) { FeedView(container: container) }
                .tabItem { Label("Comunidade", systemImage: "person.2") }
            FeatureNavigation(container: container, session: session) { LibraryView(container: container) }
                .tabItem { Label("Biblioteca", systemImage: "books.vertical") }
        }
    }
}

private struct FeatureNavigation<Content: View>: View {
    let container: AppContainer
    let session: SessionStore
    @ViewBuilder let content: () -> Content
    @State private var path: [AppRoute] = []

    var body: some View {
        NavigationStack(path: $path) {
            content()
                .navigationDestination(for: AppRoute.self) { route in
                    switch route {
                    case let .book(book): BookDetailsView(book: book, api: container.api)
                    case let .libraryBook(entry): LibraryBookDetailsView(entry: entry, container: container)
                    case let .reading(entry, title): ReadingSessionView(entry: entry, title: title, container: container)
                    case let .sessionSummary(summary): SessionSummaryView(summary: summary, title: "Sua leitura")
                    case let .post(post): PostDetailsView(post: post, api: container.api)
                    case .composer: PostComposerView(api: container.api)
                    case let .profile(id): ProfileView(userId: id, container: container, session: session)
                    case let .followers(id): FollowersView(userId: id, api: container.api)
                    case .theme: SettingsView(container: container, session: session)
                    }
                }
        }
    }
}

private struct QuickReadView: View {
    let container: AppContainer
    @State private var entry: LibraryBook?
    @State private var book: Book?
    var body: some View {
        VStack(spacing: 24) {
            if let entry, let book {
                BookCover(url: book.coverUrl, width: 150, height: 220)
                VStack { Text("Pronto para mais um capítulo?").font(.yomoraTitle).multilineTextAlignment(.center); Text(book.title).foregroundStyle(.secondary) }
                NavigationLink(value: AppRoute.reading(entry, title: book.title)) {
                    Label("Iniciar leitura", systemImage: "play.fill").font(.headline).frame(maxWidth: .infinity, minHeight: 54)
                        .foregroundStyle(.white).background(YomoraColor.primary, in: RoundedRectangle(cornerRadius: 14))
                }.accessibilityIdentifier("quickReadButton")
            } else {
                EmptyStateView(title: "Nenhum livro em andamento", message: "Mude um livro para Lendo e ele aparecerá aqui.")
            }
        }
        .padding().navigationTitle("Ler")
        .task {
            let library = (try? await container.api.send(Endpoint(path: "/api/v1/library", query: [URLQueryItem(name: "status", value: ReadingStatus.reading.rawValue)]), as: [LibraryBook].self)) ?? []
            entry = library.first
            if let id = entry?.editionId { book = try? await container.api.send(Endpoint(path: "/api/v1/books/\(id)", authenticated: false), as: Book.self) }
        }
    }
}


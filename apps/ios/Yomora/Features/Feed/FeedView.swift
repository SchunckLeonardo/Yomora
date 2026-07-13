import SwiftUI

struct FeedView: View {
    let container: AppContainer
    @State private var viewModel: FeedViewModel
    @State private var showingComposer = false

    init(container: AppContainer) {
        self.container = container
        _viewModel = State(initialValue: FeedViewModel(api: container.api))
    }

    var body: some View {
        VStack(spacing: 0) {
            Picker("Feed", selection: $viewModel.kind) {
                ForEach(FeedViewModel.FeedKind.allCases, id: \.self) { Text($0.rawValue).tag($0) }
            }.pickerStyle(.segmented).padding()
            Group {
                switch viewModel.state {
                case .idle, .loading: ScrollView { LazyVStack { ForEach(0..<3, id: \.self) { _ in LoadingSkeleton() } }.padding() }
                case let .error(message): ErrorStateView(message: message) { Task { await viewModel.load() } }
                case .loaded:
                    if viewModel.posts.isEmpty { EmptyStateView(title: "Uma comunidade tranquila", message: "Siga leitores ou publique sua primeira recomendação.", systemImage: "person.2") }
                    else {
                        ScrollView {
                            LazyVStack(spacing: 16) {
                                ForEach(viewModel.posts) { post in
                                    NavigationLink(value: AppRoute.post(post)) {
                                        PostCard(post: post) { Task { await viewModel.like(post) } }
                                    }.buttonStyle(.plain)
                                }
                            }.padding()
                        }
                    }
                }
            }
        }
        .navigationTitle("Comunidade")
        .toolbar { ToolbarItem(placement: .topBarTrailing) { Button { showingComposer = true } label: { Label("Publicar", systemImage: "square.and.pencil") } } }
        .sheet(isPresented: $showingComposer, onDismiss: { Task { await viewModel.load() } }) {
            NavigationStack { PostComposerView(api: container.api) }
        }
        .task(id: viewModel.kind) { await viewModel.load() }
        .refreshable { await viewModel.load() }
        .background(YomoraColor.warmPaper.opacity(0.45))
    }
}

struct PostDetailsView: View {
    let post: Post
    let api: any APIClientProtocol
    @State private var comments: [Comment] = []
    @State private var text = ""
    var body: some View {
        VStack {
            ScrollView {
                VStack(spacing: 16) {
                    PostCard(post: post)
                    ForEach(comments) { comment in
                        HStack(alignment: .top) {
                            UserAvatar(url: nil, size: 34)
                            Text(comment.text).frame(maxWidth: .infinity, alignment: .leading)
                        }.padding().background(.background, in: RoundedRectangle(cornerRadius: 14))
                    }
                }.padding()
            }
            HStack {
                TextField("Escreva um comentário", text: $text)
                Button("Enviar") { Task { await comment() } }.disabled(text.trimmingCharacters(in: .whitespaces).isEmpty)
            }.padding().background(.bar)
        }
        .navigationTitle("Publicação").navigationBarTitleDisplayMode(.inline)
        .task { await loadComments() }
    }

    private func loadComments() async {
        comments = (try? await api.send(Endpoint(path: "/api/v1/posts/\(post.id)/comments", authenticated: false), as: [Comment].self)) ?? []
    }

    private func comment() async {
        struct Body: Encodable { let text: String }
        var endpoint = Endpoint(path: "/api/v1/posts/\(post.id)/comments", method: .post)
        endpoint.body = try? Endpoint.json(Body(text: text))
        if let value = try? await api.send(endpoint, as: Comment.self) { comments.append(value); text = "" }
    }
}


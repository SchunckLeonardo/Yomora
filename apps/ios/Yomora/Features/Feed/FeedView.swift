import SwiftUI

struct FeedView: View {
    let container: AppContainer
    @State private var viewModel: FeedViewModel
    @State private var showingComposer = false
    @State private var selectedPost: Post?

    init(container: AppContainer) {
        self.container = container
        _viewModel = State(initialValue: FeedViewModel(api: container.api))
    }

    var body: some View {
        VStack(spacing: 0) {
            Picker("Feed", selection: $viewModel.kind) {
                ForEach(FeedViewModel.FeedKind.allCases, id: \.self) { Text($0.rawValue).tag($0) }
            }
            .pickerStyle(.segmented)
            .padding(.horizontal, YomoraSpacing.md)
            .padding(.vertical, 12)
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
                                    PostCard(
                                        post: post,
                                        isLiked: viewModel.isLiked(post),
                                        onOpen: { selectedPost = post },
                                        onLike: { Task { await viewModel.toggleLike(post) } },
                                        onComment: { selectedPost = post }
                                    )
                                }
                            }
                            .padding(.horizontal, YomoraSpacing.md)
                            .padding(.vertical, 10)
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
        .navigationDestination(item: $selectedPost) { post in
            PostDetailsView(
                post: post,
                initiallyLiked: viewModel.isLiked(post),
                api: container.api,
                onPostChange: { updated, liked in viewModel.apply(updated, liked: liked) }
            )
        }
        .task(id: viewModel.kind) { await viewModel.load() }
        .refreshable { await viewModel.load() }
        .background(YomoraColor.canvas)
    }
}

struct PostDetailsView: View {
    let post: Post
    let api: any APIClientProtocol
    let onPostChange: ((Post, Bool?) -> Void)?
    @State private var currentPost: Post
    @State private var comments: [Comment] = []
    @State private var text = ""
    @State private var isLiked: Bool
    @State private var isLoadingComments = true
    @State private var isSending = false
    @State private var interactionError: String?
    @State private var highlightedCommentID: UUID?
    @FocusState private var isComposerFocused: Bool

    init(
        post: Post,
        initiallyLiked: Bool = false,
        api: any APIClientProtocol,
        onPostChange: ((Post, Bool?) -> Void)? = nil
    ) {
        self.post = post
        self.api = api
        self.onPostChange = onPostChange
        _currentPost = State(initialValue: post)
        _isLiked = State(initialValue: initiallyLiked)
    }

    var body: some View {
        ScrollViewReader { proxy in
            ScrollView {
                LazyVStack(alignment: .leading, spacing: YomoraSpacing.md) {
                    PostCard(
                        post: currentPost,
                        isLiked: isLiked,
                        onLike: { Task { await toggleLike() } },
                        onComment: { isComposerFocused = true }
                    )

                    HStack(alignment: .firstTextBaseline) {
                        Text("Conversa").font(.yomoraHeading)
                        Text("\(comments.count)")
                            .font(.subheadline.weight(.semibold))
                            .foregroundStyle(YomoraColor.textSecondary)
                        Spacer()
                    }
                    .padding(.top, 4)

                    if isLoadingComments {
                        HStack(spacing: 10) {
                            ProgressView()
                            Text("Carregando a conversa…").foregroundStyle(YomoraColor.textSecondary)
                        }
                        .frame(maxWidth: .infinity, minHeight: 90)
                    } else if comments.isEmpty {
                        VStack(spacing: 8) {
                            Image(systemName: "bubble.left.and.bubble.right")
                                .font(.title2)
                                .foregroundStyle(YomoraColor.sereneTeal)
                            Text("Comece uma conversa tranquila").font(.headline)
                            Text("Seu comentário pode abrir uma nova leitura.")
                                .font(.subheadline)
                                .foregroundStyle(YomoraColor.textSecondary)
                                .multilineTextAlignment(.center)
                        }
                        .frame(maxWidth: .infinity, minHeight: 150)
                        .padding()
                        .background(YomoraColor.surface, in: RoundedRectangle(cornerRadius: YomoraRadius.card))
                    } else {
                        ForEach(comments) { comment in
                            CommentCard(comment: comment, highlighted: highlightedCommentID == comment.id)
                                .id(comment.id)
                        }
                    }

                    if let interactionError {
                        Label(interactionError, systemImage: "exclamationmark.circle")
                            .font(.footnote)
                            .foregroundStyle(YomoraColor.danger)
                            .padding(12)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .background(YomoraColor.danger.opacity(0.10), in: RoundedRectangle(cornerRadius: 12))
                    }
                }
                .padding(YomoraSpacing.md)
                .padding(.bottom, 8)
            }
            .onChange(of: highlightedCommentID) { _, id in
                guard let id else { return }
                withAnimation(.easeOut(duration: 0.3)) { proxy.scrollTo(id, anchor: .bottom) }
            }
        }
        .background(YomoraColor.canvas)
        .safeAreaInset(edge: .bottom) { composer }
        .navigationTitle("Publicação")
        .navigationBarTitleDisplayMode(.inline)
        .task { await loadComments() }
    }

    private var composer: some View {
        HStack(alignment: .bottom, spacing: 10) {
            UserAvatar(url: nil, size: 34)
            TextField("Escreva um comentário…", text: $text, axis: .vertical)
                .lineLimit(1...4)
                .focused($isComposerFocused)
                .submitLabel(.send)
                .onSubmit { Task { await comment() } }
                .padding(.horizontal, 14)
                .padding(.vertical, 11)
                .background(YomoraColor.surfaceElevated, in: RoundedRectangle(cornerRadius: 18))
                .overlay(RoundedRectangle(cornerRadius: 18).stroke(isComposerFocused ? YomoraColor.sereneTeal : YomoraColor.outline))
                .accessibilityIdentifier("commentField")
            Button { Task { await comment() } } label: {
                Group {
                    if isSending { ProgressView().tint(YomoraColor.onInteractive) }
                    else { Image(systemName: "arrow.up").font(.headline) }
                }
                .frame(width: 44, height: 44)
                .foregroundStyle(YomoraColor.onInteractive)
                .background(YomoraColor.interactiveFill, in: Circle())
            }
            .disabled(trimmedComment.isEmpty || isSending)
            .opacity(trimmedComment.isEmpty ? 0.5 : 1)
            .accessibilityLabel("Enviar comentário")
            .accessibilityIdentifier("sendCommentButton")
        }
        .padding(.horizontal, YomoraSpacing.md)
        .padding(.vertical, 10)
        .background(.ultraThinMaterial)
        .overlay(alignment: .top) { Rectangle().fill(YomoraColor.outline.opacity(0.45)).frame(height: 0.5) }
    }

    private var trimmedComment: String {
        text.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private func loadComments() async {
        isLoadingComments = true
        do {
            comments = try await api.send(Endpoint(path: "/api/v1/posts/\(post.id)/comments"), as: [Comment].self)
            interactionError = nil
        } catch {
            interactionError = error.localizedDescription
        }
        isLoadingComments = false
    }

    private func comment() async {
        let message = trimmedComment
        guard !message.isEmpty, !isSending else { return }
        struct Body: Encodable { let text: String }
        var endpoint = Endpoint(path: "/api/v1/posts/\(post.id)/comments", method: .post)
        endpoint.body = try? Endpoint.json(Body(text: message))
        isSending = true
        do {
            let value = try await api.send(endpoint, as: Comment.self)
            withAnimation(.easeOut(duration: 0.25)) { comments.append(value) }
            currentPost = currentPost.updatingEngagement(commentCount: currentPost.commentCount + 1)
            onPostChange?(currentPost, nil)
            text = ""
            highlightedCommentID = value.id
            interactionError = nil
        } catch {
            interactionError = error.localizedDescription
        }
        isSending = false
    }

    private func toggleLike() async {
        let shouldLike = !isLiked
        var endpoint = Endpoint(path: "/api/v1/posts/\(post.id)/likes", method: shouldLike ? .post : .delete)
        if shouldLike { endpoint.body = Data("{}".utf8) }
        do {
            currentPost = try await api.send(endpoint, as: Post.self)
            withAnimation(.spring(response: 0.32, dampingFraction: 0.72)) { isLiked = shouldLike }
            onPostChange?(currentPost, shouldLike)
            interactionError = nil
        } catch {
            interactionError = error.localizedDescription
        }
    }
}

import SwiftUI

struct SearchView: View {
    @State private var viewModel: SearchViewModel

    init(container: AppContainer) {
        _viewModel = State(initialValue: SearchViewModel(api: container.api, cache: container.bookCache))
    }

    var body: some View {
        VStack(spacing: 0) {
            SearchBar(viewModel: viewModel)

            Group {
                switch viewModel.state {
                case .idle:
                    EmptyStateView(title: "Descubra sua próxima história", message: "Busque por título, autor ou ISBN.", systemImage: "sparkles")
                case .loading:
                    ScrollView { LazyVStack { ForEach(0..<4, id: \.self) { _ in LoadingSkeleton() } }.padding() }
                case let .error(message):
                    ErrorStateView(message: message) { Task { await viewModel.search() } }
                case .loaded:
                    if viewModel.books.isEmpty {
                        EmptyStateView(title: "Nenhum livro encontrado", message: "Tente outra busca ou cadastre uma edição manualmente.")
                    } else {
                        ScrollView {
                            LazyVStack(spacing: 14) {
                                ForEach(viewModel.books) { book in
                                    NavigationLink(value: AppRoute.book(book)) { BookCard(book: book) }
                                        .buttonStyle(.plain).accessibilityIdentifier("bookResult_\(book.editionId)")
                                }
                            }.padding()
                        }
                    }
                }
            }
        }
        .navigationTitle("Descobrir")
        .background(YomoraColor.canvas)
    }
}

private struct SearchBar: View {
    @Bindable var viewModel: SearchViewModel

    var body: some View {
        HStack {
            Image(systemName: "magnifyingglass").foregroundStyle(.secondary)
            TextField("Título, autor ou ISBN", text: $viewModel.query)
                .textInputAutocapitalization(.never)
                .autocorrectionDisabled()
                .submitLabel(.search)
                .onSubmit { Task { await viewModel.search() } }
                .accessibilityIdentifier("bookSearchField")
            if !viewModel.query.isEmpty {
                Button { viewModel.query = "" } label: { Image(systemName: "xmark.circle.fill") }
            }
        }
        .padding()
        .background(YomoraColor.surface, in: RoundedRectangle(cornerRadius: 14))
        .padding()
    }
}

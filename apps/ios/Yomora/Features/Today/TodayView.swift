import SwiftUI

struct TodayView: View {
    let container: AppContainer
    let user: UserProfile?
    @State private var viewModel: TodayViewModel
    @AppStorage("dailyGoalMinutes") private var dailyMinutes = 20
    @AppStorage("weeklyGoalDays") private var weeklyDays = 5

    init(container: AppContainer, user: UserProfile?) {
        self.container = container
        self.user = user
        _viewModel = State(initialValue: TodayViewModel(api: container.api))
    }

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 20) {
                VStack(alignment: .leading, spacing: 4) {
                    Text("HOJE").font(.caption.weight(.bold)).tracking(2).foregroundStyle(YomoraColor.progressGold)
                    Text("Olá, \(user?.name.components(separatedBy: " ").first ?? "leitor")").font(.yomoraTitle)
                    Text(Date.now.formatted(.dateTime.weekday(.wide).day().month(.wide))).foregroundStyle(.secondary)
                }
                switch viewModel.state {
                case .idle, .loading:
                    LoadingSkeleton(); LoadingSkeleton()
                case let .error(message):
                    ErrorStateView(message: message) { Task { await reload() } }
                case .loaded:
                    ReadingGoalCard(minutes: viewModel.goal.dailyMinutes,
                                    completed: viewModel.statistics?.minutesToday ?? 0)
                    if let entry = viewModel.library.first(where: { $0.status == .reading }),
                       let book = viewModel.featuredBook {
                        VStack(alignment: .leading, spacing: 12) {
                            Text("Continue lendo").font(.yomoraHeading)
                            NavigationLink(value: AppRoute.reading(entry, title: book.title)) {
                                BookCard(book: book, progress: progress(entry: entry, book: book))
                            }.buttonStyle(.plain).accessibilityIdentifier("continueReadingCard")
                        }
                    } else {
                        EmptyStateView(title: "Escolha sua próxima leitura",
                                       message: "Adicione um livro e transforme hoje em mais um dia lido.")
                    }
                    consistency
                }
            }
            .padding()
        }
        .background(YomoraColor.canvas)
        .navigationTitle("Yomora")
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                NavigationLink(value: AppRoute.profile(user?.id ?? UUID())) { UserAvatar(url: user?.avatarUrl, size: 34) }
            }
        }
        .refreshable { await reload() }
        .onReceive(NotificationCenter.default.publisher(for: .readingGoalDidChange)) { notification in
            guard let goal = notification.object as? ReadingGoal else { return }
            viewModel.applyPersistedGoal(goal)
        }
        .task { if viewModel.state == .idle { await reload() } }
    }

    private var consistency: some View {
        HStack {
            VStack(alignment: .leading) {
                Text("Ritmo dos últimos 30 dias").font(.headline)
                Text("\(viewModel.statistics?.daysReadLast30 ?? 0) dias lidos · sequência de \(viewModel.statistics?.currentStreak ?? 0)")
                    .font(.subheadline).foregroundStyle(.secondary)
            }
            Spacer()
            Text("\(Int(viewModel.statistics?.pacePercent ?? 0))%").font(.title2.bold()).foregroundStyle(YomoraColor.progressGold)
        }
        .padding().background(YomoraColor.surface, in: RoundedRectangle(cornerRadius: YomoraRadius.card))
    }

    private func progress(entry: LibraryBook, book: Book) -> Double {
        guard let pages = book.pageCount, pages > 0 else { return 0 }
        return min(1, Double(entry.currentPage) / Double(pages))
    }

    private func reload() async { await viewModel.load(defaultMinutes: dailyMinutes, weeklyDays: weeklyDays) }
}

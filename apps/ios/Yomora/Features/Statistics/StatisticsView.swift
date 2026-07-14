import SwiftUI

struct StatisticsView: View {
    let api: any APIClientProtocol
    @State private var statistics: StatisticsSummary?
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                Text("Seu ritmo, sem pressa").font(.yomoraTitle)
                if let value = statistics {
                    ReadingGoalCard(minutes: max(value.minutesToday, 20), completed: value.minutesToday)
                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 14) {
                        tile("Hoje", "\(value.minutesToday) min", "sun.max")
                        tile("Semana", "\(value.minutesThisWeek) min", "calendar")
                        tile("Páginas", "\(value.pagesThisWeek)", "book.pages")
                        tile("Sequência", "\(value.currentStreak) dias", "flame")
                        tile("Melhor", "\(value.bestStreak) dias", "trophy")
                        tile("Sessões", "\(value.totalSessions)", "timer")
                    }
                    Text("Gêneros mais lidos").font(.yomoraHeading)
                    ForEach(value.topGenres.sorted(by: { $0.value > $1.value }), id: \.key) { genre, count in
                        HStack { Text(genre); Spacer(); Text("\(count)").foregroundStyle(.secondary) }
                    }
                } else { LoadingSkeleton(); LoadingSkeleton() }
            }.padding()
        }
        .navigationTitle("Estatísticas")
        .task { statistics = try? await api.send(Endpoint(path: "/api/v1/statistics/summary"), as: StatisticsSummary.self) }
        .background(YomoraColor.canvas)
    }
    private func tile(_ title: String, _ value: String, _ icon: String) -> some View {
        VStack(alignment: .leading, spacing: 9) { Image(systemName: icon).foregroundStyle(YomoraColor.progressGold); Text(value).font(.title2.bold()); Text(title).font(.caption).foregroundStyle(.secondary) }
            .frame(maxWidth: .infinity, minHeight: 110, alignment: .leading).padding().background(YomoraColor.surface, in: RoundedRectangle(cornerRadius: 16))
    }
}

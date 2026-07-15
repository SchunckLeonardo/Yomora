import SwiftUI

struct ReadingSessionView: View {
    let title: String
    @State private var viewModel: ReadingSessionViewModel

    init(entry: LibraryBook, title: String, container: AppContainer) {
        self.title = title
        _viewModel = State(initialValue: ReadingSessionViewModel(entry: entry, title: title,
                                                                 api: container.api, activity: container.readingActivity))
    }

    var body: some View {
        ZStack {
            YomoraColor.focusBackground.ignoresSafeArea()
            VStack(spacing: 28) {
                Text("MODO FOCO").font(.caption.weight(.bold)).tracking(3).foregroundStyle(YomoraColor.progressGold)
                Text(title).font(.yomoraHeading).multilineTextAlignment(.center).foregroundStyle(.white)
                Spacer()
                TimelineView(.periodic(from: .now, by: 1)) { context in
                    Text(formatted(viewModel.timer.elapsed(at: context.date)))
                        .font(.system(size: 68, weight: .light, design: .monospaced)).foregroundStyle(.white)
                        .contentTransition(.numericText()).accessibilityLabel("Tempo de leitura")
                }
                stateControls
                if viewModel.session != nil {
                    Stepper("Página alcançada: \(viewModel.endPage)", value: $viewModel.endPage, in: 0...20_000)
                        .foregroundStyle(.white).padding().background(.white.opacity(0.1), in: RoundedRectangle(cornerRadius: 14))
                    ReadingSessionNoteField(viewModel: viewModel)
                }
                Spacer()
            }
            .padding(24)
        }
        .navigationBarTitleDisplayMode(.inline)
        .navigationDestination(isPresented: Binding(
            get: { viewModel.summary != nil }, set: { if !$0 { } }
        )) {
            if let summary = viewModel.summary { SessionSummaryView(summary: summary, title: title) }
        }
    }

    @ViewBuilder private var stateControls: some View {
        switch viewModel.state {
        case .idle, .error:
            PrimaryButton(title: "Iniciar sessão", systemImage: "play.fill") { Task { await viewModel.start() } }
                .accessibilityIdentifier("timerStartButton")
        case .starting, .finishing:
            ProgressView().tint(.white).controlSize(.large)
        case .running:
            HStack(spacing: 14) {
                Button { viewModel.pause() } label: { Label("Pausar", systemImage: "pause.fill").frame(maxWidth: .infinity, minHeight: 50) }
                    .buttonStyle(.bordered).tint(.white)
                Button { Task { await viewModel.finish() } } label: { Label("Finalizar", systemImage: "stop.fill").frame(maxWidth: .infinity, minHeight: 50) }
                    .buttonStyle(.borderedProminent).tint(YomoraColor.progressGold).accessibilityIdentifier("timerFinishButton")
            }
        case .paused:
            HStack(spacing: 14) {
                Button { viewModel.resume() } label: { Label("Continuar", systemImage: "play.fill").frame(maxWidth: .infinity, minHeight: 50) }.buttonStyle(.bordered).tint(.white)
                Button { Task { await viewModel.finish() } } label: { Label("Finalizar", systemImage: "stop.fill").frame(maxWidth: .infinity, minHeight: 50) }.buttonStyle(.borderedProminent).tint(YomoraColor.progressGold)
            }
        case .finished: EmptyView()
        }
    }

    private func formatted(_ interval: TimeInterval) -> String {
        let seconds = Int(interval); return String(format: "%02d:%02d:%02d", seconds / 3600, (seconds / 60) % 60, seconds % 60)
    }
}

private struct ReadingSessionNoteField: View {
    @Bindable var viewModel: ReadingSessionViewModel

    var body: some View {
        TextField("Nota opcional da sessão", text: $viewModel.note, axis: .vertical)
            .padding()
            .background(.white, in: RoundedRectangle(cornerRadius: 14))
    }
}

struct SessionSummaryView: View {
    let summary: SessionSummary
    let title: String
    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                Image(systemName: "checkmark.seal.fill").font(.system(size: 74)).foregroundStyle(YomoraColor.progressGold)
                Text("Sessão concluída").font(.yomoraTitle)
                Text(title).foregroundStyle(.secondary)
                LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 14) {
                    metric("Tempo", "\(summary.durationMinutes) min", "clock")
                    metric("Páginas", "\(summary.pagesRead)", "book.pages")
                    metric("Velocidade", String(format: "%.0f pág/h", summary.averagePagesPerHour), "speedometer")
                    metric("Sequência", "\(summary.currentStreak) dias", "flame")
                }
                ProgressRing(progress: summary.progressPercent / 100, size: 120)
                Text("Estimativa: \(summary.estimatedSessionsRemaining) sessões restantes")
                ShareLink(item: "Li \(summary.pagesRead) páginas de \(title) no Yomora. https://yomora.app") {
                    Label("Compartilhar progresso", systemImage: "square.and.arrow.up")
                }.buttonStyle(.borderedProminent)
            }.padding()
        }
        .navigationTitle("Resumo").navigationBarBackButtonHidden(false)
    }

    private func metric(_ label: String, _ value: String, _ icon: String) -> some View {
        VStack(spacing: 8) { Image(systemName: icon).foregroundStyle(YomoraColor.progressGold); Text(value).font(.title3.bold()); Text(label).font(.caption).foregroundStyle(.secondary) }
            .frame(maxWidth: .infinity, minHeight: 110).background(YomoraColor.surface, in: RoundedRectangle(cornerRadius: 16))
    }
}

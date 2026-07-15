import SwiftUI

struct ReadingSessionView: View {
    let title: String
    let onExitToToday: () -> Void
    @State private var viewModel: ReadingSessionViewModel
    @FocusState private var noteFocused: Bool

    init(
        entry: LibraryBook,
        book: Book?,
        existingSession: ReadingSession?,
        container: AppContainer,
        onExitToToday: @escaping () -> Void
    ) {
        let title = book?.title ?? "Sua leitura"
        self.title = title
        self.onExitToToday = onExitToToday
        _viewModel = State(initialValue: ReadingSessionViewModel(
            entry: entry,
            book: book,
            title: title,
            api: container.api,
            activity: container.readingActivity,
            coordinator: container.activeReadingSession,
            existingSession: existingSession
        ))
    }

    var body: some View {
        ZStack {
            YomoraColor.focusBackground.ignoresSafeArea()
            GeometryReader { geometry in
                ScrollViewReader { proxy in
                    ScrollView {
                        VStack(spacing: 28) {
                            Text("MODO FOCO")
                                .font(.caption.weight(.bold))
                                .tracking(3)
                                .foregroundStyle(YomoraColor.progressGold)
                            Text(title)
                                .font(.yomoraHeading)
                                .multilineTextAlignment(.center)
                                .foregroundStyle(.white)
                            Spacer(minLength: 20)
                            TimelineView(.periodic(from: .now, by: 1)) { context in
                                Text(formatted(viewModel.timer.elapsed(at: context.date)))
                                    .font(.system(size: 68, weight: .light, design: .monospaced))
                                    .foregroundStyle(.white)
                                    .contentTransition(.numericText())
                                    .accessibilityLabel("Tempo de leitura")
                            }
                            stateControls
                            if viewModel.session != nil {
                                Stepper("Página alcançada: \(viewModel.endPage)", value: $viewModel.endPage, in: 0...20_000)
                                    .foregroundStyle(.white)
                                    .padding()
                                    .background(.white.opacity(0.1), in: RoundedRectangle(cornerRadius: 14))
                                    .onChange(of: viewModel.endPage) { _, _ in viewModel.scheduleProgressUpdate() }
                                if let syncError = viewModel.syncError {
                                    Text(syncError)
                                        .font(.footnote)
                                        .foregroundStyle(.white)
                                        .frame(maxWidth: .infinity, alignment: .leading)
                                }
                                ReadingSessionNoteField(viewModel: viewModel, isFocused: $noteFocused)
                                    .id("readingSessionNote")
                            }
                            Spacer(minLength: 20)
                        }
                        .frame(maxWidth: .infinity, minHeight: geometry.size.height)
                        .padding(24)
                    }
                    .scrollDismissesKeyboard(.interactively)
                    .onChange(of: noteFocused) { _, isFocused in
                        guard isFocused else { return }
                        withAnimation {
                            proxy.scrollTo("readingSessionNote", anchor: .bottom)
                        }
                    }
                }
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItemGroup(placement: .keyboard) {
                Spacer()
                Button("Concluir") { noteFocused = false }
                    .accessibilityIdentifier("dismissReadingNoteKeyboardButton")
            }
        }
        .navigationDestination(isPresented: Binding(
            get: { viewModel.summary != nil }, set: { if !$0 { } }
        )) {
            if let summary = viewModel.summary {
                SessionSummaryView(
                    summary: summary,
                    title: title,
                    note: viewModel.note,
                    onExitToToday: onExitToToday
                )
            }
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
                Button { Task { await viewModel.pause() } } label: { Label("Pausar", systemImage: "pause.fill").frame(maxWidth: .infinity, minHeight: 50) }
                    .buttonStyle(.bordered).tint(.white)
                Button { Task { await viewModel.finish() } } label: { Label("Finalizar", systemImage: "stop.fill").frame(maxWidth: .infinity, minHeight: 50) }
                    .buttonStyle(.borderedProminent).tint(YomoraColor.progressGold).accessibilityIdentifier("timerFinishButton")
            }
        case .paused:
            HStack(spacing: 14) {
                Button { Task { await viewModel.resume() } } label: { Label("Continuar", systemImage: "play.fill").frame(maxWidth: .infinity, minHeight: 50) }.buttonStyle(.bordered).tint(.white)
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
    var isFocused: FocusState<Bool>.Binding

    var body: some View {
        VStack(alignment: .leading, spacing: YomoraSpacing.sm) {
            Text("Nota da sessão")
                .font(.subheadline.weight(.semibold))
                .foregroundStyle(.white)
            ZStack(alignment: .topLeading) {
                if viewModel.note.isEmpty {
                    Text("Registre uma ideia, citação ou sensação desta leitura")
                        .foregroundStyle(YomoraColor.focusInputPlaceholder)
                        .padding(.horizontal, 13)
                        .padding(.vertical, 15)
                        .allowsHitTesting(false)
                }
                TextEditor(text: $viewModel.note)
                    .scrollContentBackground(.hidden)
                    .foregroundStyle(YomoraColor.focusInputText)
                    .tint(YomoraColor.focusInputText)
                    .padding(8)
                    .focused(isFocused)
                    .accessibilityLabel("Nota da sessão")
                    .accessibilityIdentifier("readingSessionNoteField")
            }
            .frame(minHeight: 110)
            .background(YomoraColor.focusInputBackground, in: RoundedRectangle(cornerRadius: 14))
        }
    }
}

struct SessionSummaryView: View {
    let summary: SessionSummary
    let title: String
    let note: String?
    let onExitToToday: () -> Void

    init(
        summary: SessionSummary,
        title: String,
        note: String? = nil,
        onExitToToday: @escaping () -> Void
    ) {
        self.summary = summary
        self.title = title
        let trimmed = note?.trimmingCharacters(in: .whitespacesAndNewlines)
        self.note = trimmed?.isEmpty == false ? trimmed : nil
        self.onExitToToday = onExitToToday
    }

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
                if let note {
                    VStack(alignment: .leading, spacing: YomoraSpacing.sm) {
                        Label("Nota da sessão", systemImage: "note.text")
                            .font(.headline)
                            .foregroundStyle(YomoraColor.sereneTeal)
                        Text(note)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }
                    .padding()
                    .background(YomoraColor.surface, in: RoundedRectangle(cornerRadius: YomoraRadius.card))
                }
                ShareLink(item: "Li \(summary.pagesRead) páginas de \(title) no Yomora. https://yomora.app") {
                    Label("Compartilhar progresso", systemImage: "square.and.arrow.up")
                }.buttonStyle(.borderedProminent)
            }.padding()
        }
        .navigationTitle("Resumo")
        .navigationBarBackButtonHidden(true)
        .toolbar {
            ToolbarItem(placement: .topBarLeading) {
                Button(action: onExitToToday) {
                    Image(systemName: "chevron.left")
                }
                .accessibilityLabel("Voltar para Hoje")
            }
        }
    }

    private func metric(_ label: String, _ value: String, _ icon: String) -> some View {
        VStack(spacing: 8) { Image(systemName: icon).foregroundStyle(YomoraColor.progressGold); Text(value).font(.title3.bold()); Text(label).font(.caption).foregroundStyle(.secondary) }
            .frame(maxWidth: .infinity, minHeight: 110).background(YomoraColor.surface, in: RoundedRectangle(cornerRadius: 16))
    }
}

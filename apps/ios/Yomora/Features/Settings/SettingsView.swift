import SwiftUI

extension Notification.Name {
    static let readingGoalDidChange = Notification.Name("app.yomora.readingGoalDidChange")
}

struct SettingsView: View {
    let container: AppContainer
    let session: SessionStore
    @AppStorage("dailyGoalMinutes") private var dailyMinutes = 20
    @AppStorage("weeklyGoalDays") private var weeklyDays = 5
    @AppStorage(ReadingReminderStorage.enabledKey) private var reminderEnabled = false
    @AppStorage(ReadingReminderStorage.minutesKey) private var reminderMinutes = ReadingReminderStorage.defaultMinutesAfterMidnight
    @AppStorage(ReadingReminderStorage.weekdayMaskKey) private var reminderWeekdayMask = ReadingReminderStorage.allWeekdaysMask
    @State private var showingThemes = false
    @State private var showingDelete = false
    @State private var showingReminderDenied = false
    @State private var reminderMessage: String?

    var body: some View {
        Form {
            Section("Leitura") {
                Stepper("Meta diária: \(dailyMinutes) min", value: $dailyMinutes, in: 5...120, step: 5)
                Stepper("Meta semanal: \(weeklyDays) dias", value: $weeklyDays, in: 1...7)
                Button("Salvar metas") { Task { await saveGoal() } }
                NavigationLink { StatisticsView(api: container.api) } label: { Label("Estatísticas", systemImage: "chart.bar") }
            }
            Section("Lembretes") {
                Toggle("Lembrar de ler", isOn: Binding(
                    get: { reminderEnabled },
                    set: { enabled in
                        reminderEnabled = enabled
                        Task { await configureReminder(enabled: enabled) }
                    }
                ))
                if reminderEnabled {
                    DatePicker("Horário", selection: reminderTime, displayedComponents: .hourAndMinute)
                    VStack(alignment: .leading, spacing: YomoraSpacing.sm) {
                        Text("Dias da semana").font(.subheadline)
                        HStack(spacing: 7) {
                            ForEach(Array(Calendar.current.veryShortWeekdaySymbols.enumerated()), id: \.offset) { index, symbol in
                                let weekday = index + 1
                                Button { toggleWeekday(weekday) } label: {
                                    Text(symbol.uppercased())
                                        .font(.caption.weight(.semibold))
                                        .frame(width: 30, height: 30)
                                        .foregroundStyle(isWeekdaySelected(weekday) ? YomoraColor.onInteractive : YomoraColor.textSecondary)
                                        .background(
                                            isWeekdaySelected(weekday) ? YomoraColor.interactiveFill : YomoraColor.surfaceElevated,
                                            in: Circle()
                                        )
                                }
                                .buttonStyle(.plain)
                                .accessibilityLabel(Calendar.current.weekdaySymbols[index])
                                .accessibilityAddTraits(isWeekdaySelected(weekday) ? .isSelected : [])
                            }
                        }
                    }
                    Button("Salvar lembrete") { Task { await saveReminder() } }
                }
                if let reminderMessage {
                    Text(reminderMessage).font(.footnote).foregroundStyle(.secondary)
                } else {
                    Text("O Yomora só pedirá permissão quando você ativar este recurso.")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                }
            }
            Section("Aparência") { Button { showingThemes = true } label: { Label("Tema e destaque", systemImage: "paintpalette") } }
            Section("Conta") {
                Button("Sair") { Task { await session.logout() } }
                Button("Excluir minha conta", role: .destructive) { showingDelete = true }
            }
            Section("Sobre") { LabeledContent("Versão", value: "1.0 MVP") }
        }
        .navigationTitle("Configurações")
        .sheet(isPresented: $showingThemes) { NavigationStack { ThemeSelectionView() } }
        .confirmationDialog("Excluir a conta e todos os dados?", isPresented: $showingDelete, titleVisibility: .visible) {
            Button("Excluir definitivamente", role: .destructive) { Task { try? await container.api.sendVoid(Endpoint(path: "/api/v1/users/me", method: .delete)); await session.logout() } }
        }
        .alert("Lembretes desativados", isPresented: $showingReminderDenied) {
            Button("Abrir Ajustes") {
                if let url = URL(string: UIApplication.openSettingsURLString) {
                    UIApplication.shared.open(url)
                }
            }
            Button("Agora não", role: .cancel) { }
        } message: {
            Text("Autorize as notificações nos Ajustes do iPhone para receber seus lembretes de leitura.")
        }
    }

    private func saveGoal() async {
        struct Body: Encodable { let dailyMinutes: Int; let weeklyDays: Int; let dailyPages: Int? }
        var endpoint = Endpoint(path: "/api/v1/reading-goals", method: .put)
        endpoint.body = try? Endpoint.json(Body(dailyMinutes: dailyMinutes, weeklyDays: weeklyDays, dailyPages: nil))
        if let savedGoal = try? await container.api.send(endpoint, as: ReadingGoal.self) {
            dailyMinutes = savedGoal.dailyMinutes
            weeklyDays = savedGoal.weeklyDays
            NotificationCenter.default.post(name: .readingGoalDidChange, object: savedGoal)
        }
    }

    private var reminderTime: Binding<Date> {
        Binding(
            get: {
                let hour = reminderMinutes / 60
                let minute = reminderMinutes % 60
                return Calendar.current.date(bySettingHour: hour, minute: minute, second: 0, of: .now) ?? .now
            },
            set: { date in
                let components = Calendar.current.dateComponents([.hour, .minute], from: date)
                reminderMinutes = (components.hour ?? 20) * 60 + (components.minute ?? 0)
            }
        )
    }

    private func configureReminder(enabled: Bool) async {
        guard enabled else {
            await container.readingReminders.cancelAll()
            reminderMessage = "Lembretes desativados."
            NotificationCenter.default.post(name: .readingReminderDidChange, object: nil)
            return
        }
        guard await container.readingReminders.requestPermission() else {
            reminderEnabled = false
            await container.readingReminders.cancelAll()
            reminderMessage = nil
            showingReminderDenied = true
            NotificationCenter.default.post(name: .readingReminderDidChange, object: nil)
            return
        }
        await saveReminder()
    }

    private func saveReminder() async {
        let preferences = ReadingReminderPreferences(
            enabled: reminderEnabled,
            minutesAfterMidnight: reminderMinutes,
            weekdayMask: reminderWeekdayMask
        )
        await container.readingReminders.rebuild(preferences, skippingToday: false)
        if let next = await container.readingReminders.nextReminderDate() {
            reminderMessage = "Próximo lembrete: \(next.formatted(.dateTime.weekday(.wide).hour().minute()))."
        } else if reminderEnabled {
            reminderMessage = "Nenhum lembrete futuro para os dias selecionados."
        }
        NotificationCenter.default.post(name: .readingReminderDidChange, object: nil)
    }

    private func isWeekdaySelected(_ weekday: Int) -> Bool {
        reminderWeekdayMask & (1 << (weekday - 1)) != 0
    }

    private func toggleWeekday(_ weekday: Int) {
        let nextMask = reminderWeekdayMask ^ (1 << (weekday - 1))
        guard nextMask != 0 else { return }
        reminderWeekdayMask = nextMask
    }
}

private struct ThemeSelectionView: View {
    @Environment(\.dismiss) private var dismiss
    @AppStorage("theme") private var themeRaw = AppTheme.system.rawValue
    @AppStorage("accent") private var accentRaw = AccentChoice.teal.rawValue
    var body: some View {
        ThemePicker(theme: Binding(get: { AppTheme(rawValue: themeRaw) ?? .system }, set: { themeRaw = $0.rawValue }),
                    accent: Binding(get: { AccentChoice(rawValue: accentRaw) ?? .teal }, set: { accentRaw = $0.rawValue }))
            .navigationTitle("Tema")
            .toolbar { ToolbarItem(placement: .confirmationAction) { Button("Concluir") { dismiss() } } }
    }
}

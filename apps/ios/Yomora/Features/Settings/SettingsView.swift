import SwiftUI

struct SettingsView: View {
    let container: AppContainer
    let session: SessionStore
    @AppStorage("dailyGoalMinutes") private var dailyMinutes = 20
    @AppStorage("weeklyGoalDays") private var weeklyDays = 5
    @State private var showingThemes = false
    @State private var showingDelete = false

    var body: some View {
        Form {
            Section("Leitura") {
                Stepper("Meta diária: \(dailyMinutes) min", value: $dailyMinutes, in: 5...120, step: 5)
                Stepper("Meta semanal: \(weeklyDays) dias", value: $weeklyDays, in: 1...7)
                Button("Salvar metas") { Task { await saveGoal() } }
                NavigationLink { StatisticsView(api: container.api) } label: { Label("Estatísticas", systemImage: "chart.bar") }
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
    }

    private func saveGoal() async {
        struct Body: Encodable { let dailyMinutes: Int; let weeklyDays: Int; let dailyPages: Int? }
        var endpoint = Endpoint(path: "/api/v1/reading-goals", method: .put)
        endpoint.body = try? Endpoint.json(Body(dailyMinutes: dailyMinutes, weeklyDays: weeklyDays, dailyPages: nil))
        _ = try? await container.api.send(endpoint, as: ReadingGoal.self)
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


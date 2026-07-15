import SwiftUI
import SwiftData

@main
struct YomoraApp: App {
    @UIApplicationDelegateAdaptor(YomoraNotificationDelegate.self) private var notificationDelegate
    @AppStorage("theme") private var themeRaw = AppTheme.system.rawValue
    @AppStorage("accent") private var accentRaw = AccentChoice.teal.rawValue
    private let container = AppContainer.live()

    var body: some Scene {
        WindowGroup {
            RootView(container: container)
                .preferredColorScheme(AppTheme(rawValue: themeRaw)?.colorScheme)
                .tint(AccentChoice(rawValue: accentRaw)?.color ?? YomoraColor.sereneTeal)
                .background((AppTheme(rawValue: themeRaw) == .sepia ? YomoraColor.sepia : Color.clear).ignoresSafeArea())
                .onOpenURL { url in
                    guard url.scheme == "yomora" else { return }
                    if url.host == "reading" {
                        container.navigation.open(.activeReadingSession)
                    }
                }
        }
        .modelContainer(container.modelContainer)
    }
}

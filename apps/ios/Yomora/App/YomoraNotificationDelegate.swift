import UIKit
@preconcurrency import UserNotifications

final class YomoraNotificationDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        UNUserNotificationCenter.current().delegate = self
        return true
    }

    nonisolated func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification
    ) async -> UNNotificationPresentationOptions {
        [.banner, .sound]
    }

    nonisolated func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse
    ) async {
        guard response.notification.request.content.userInfo["destination"] as? String == "today" else { return }
        await MainActor.run {
            UserDefaults.standard.set(true, forKey: ReadingReminderStorage.pendingTodayOpenKey)
            NotificationCenter.default.post(name: .readingReminderOpened, object: nil)
        }
    }
}

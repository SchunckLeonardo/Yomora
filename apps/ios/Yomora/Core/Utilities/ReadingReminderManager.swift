import Foundation
@preconcurrency import UserNotifications

extension Notification.Name {
    static let readingReminderDidChange = Notification.Name("app.yomora.readingReminderDidChange")
    static let readingReminderOpened = Notification.Name("app.yomora.readingReminderOpened")
    static let readingSessionDidFinish = Notification.Name("app.yomora.readingSessionDidFinish")
}

enum ReadingReminderStorage {
    static let enabledKey = "readingReminderEnabled"
    static let minutesKey = "readingReminderMinutesAfterMidnight"
    static let weekdayMaskKey = "readingReminderWeekdayMask"
    static let pendingTodayOpenKey = "readingReminderPendingTodayOpen"
    static let defaultMinutesAfterMidnight = 20 * 60
    static let allWeekdaysMask = 0b1111111
}

struct ReadingReminderPreferences: Sendable, Equatable {
    let enabled: Bool
    let minutesAfterMidnight: Int
    let weekdayMask: Int

    static func stored(in defaults: UserDefaults = .standard) -> ReadingReminderPreferences {
        ReadingReminderPreferences(
            enabled: defaults.bool(forKey: ReadingReminderStorage.enabledKey),
            minutesAfterMidnight: defaults.object(forKey: ReadingReminderStorage.minutesKey) as? Int
                ?? ReadingReminderStorage.defaultMinutesAfterMidnight,
            weekdayMask: defaults.object(forKey: ReadingReminderStorage.weekdayMaskKey) as? Int
                ?? ReadingReminderStorage.allWeekdaysMask
        )
    }
}

struct PlannedReadingReminder: Sendable, Equatable {
    let identifier: String
    let date: Date
}

enum ReadingReminderPlanner {
    static let identifierPrefix = "app.yomora.reading-reminder."

    static func plan(
        preferences: ReadingReminderPreferences,
        now: Date = .now,
        skippingToday: Bool,
        calendar: Calendar = .current
    ) -> [PlannedReadingReminder] {
        guard preferences.enabled, preferences.weekdayMask != 0 else { return [] }
        let minutes = min(max(preferences.minutesAfterMidnight, 0), 23 * 60 + 59)
        let hour = minutes / 60
        let minute = minutes % 60
        let today = calendar.startOfDay(for: now)

        return (0..<42).compactMap { offset in
            guard let day = calendar.date(byAdding: .day, value: offset, to: today),
                  !(skippingToday && offset == 0),
                  isSelected(day, mask: preferences.weekdayMask, calendar: calendar),
                  let date = calendar.date(bySettingHour: hour, minute: minute, second: 0, of: day),
                  date > now else { return nil }
            let components = calendar.dateComponents([.year, .month, .day], from: date)
            guard let year = components.year, let month = components.month, let dayOfMonth = components.day else {
                return nil
            }
            return PlannedReadingReminder(
                identifier: identifierPrefix + String(format: "%04d-%02d-%02d", year, month, dayOfMonth),
                date: date
            )
        }
    }

    private static func isSelected(_ date: Date, mask: Int, calendar: Calendar) -> Bool {
        let weekday = calendar.component(.weekday, from: date)
        return mask & (1 << (weekday - 1)) != 0
    }
}

enum ReadingReminderPermission: Sendable, Equatable {
    case notDetermined
    case allowed
    case denied
}

protocol ReadingReminderManaging: Sendable {
    func permission() async -> ReadingReminderPermission
    func requestPermission() async -> Bool
    func rebuild(_ preferences: ReadingReminderPreferences, skippingToday: Bool) async
    func cancelAll() async
    func nextReminderDate() async -> Date?
}

struct NoopReadingReminderManager: ReadingReminderManaging {
    func permission() async -> ReadingReminderPermission { .allowed }
    func requestPermission() async -> Bool { true }
    func rebuild(_ preferences: ReadingReminderPreferences, skippingToday: Bool) async { }
    func cancelAll() async { }
    func nextReminderDate() async -> Date? { nil }
}

final class LocalReadingReminderManager: ReadingReminderManaging, @unchecked Sendable {
    private let center: UNUserNotificationCenter

    init(center: UNUserNotificationCenter = .current()) {
        self.center = center
    }

    func permission() async -> ReadingReminderPermission {
        let settings = await center.notificationSettings()
        return switch settings.authorizationStatus {
        case .notDetermined: .notDetermined
        case .denied: .denied
        case .authorized, .provisional, .ephemeral: .allowed
        @unknown default: .denied
        }
    }

    func requestPermission() async -> Bool {
        switch await permission() {
        case .allowed: return true
        case .denied: return false
        case .notDetermined:
            return (try? await center.requestAuthorization(options: [.alert, .badge, .sound])) ?? false
        }
    }

    func rebuild(_ preferences: ReadingReminderPreferences, skippingToday: Bool) async {
        await cancelAll()
        guard preferences.enabled, await permission() == .allowed else { return }
        let calendar = Calendar.current
        for reminder in ReadingReminderPlanner.plan(
            preferences: preferences,
            skippingToday: skippingToday,
            calendar: calendar
        ) {
            let content = UNMutableNotificationContent()
            content.title = "Um capítulo cabe no seu dia"
            content.body = "Reserve alguns minutos para continuar sua leitura no Yomora."
            content.sound = .default
            content.userInfo = ["destination": "today"]
            let components = calendar.dateComponents([.year, .month, .day, .hour, .minute], from: reminder.date)
            let request = UNNotificationRequest(
                identifier: reminder.identifier,
                content: content,
                trigger: UNCalendarNotificationTrigger(dateMatching: components, repeats: false)
            )
            try? await center.add(request)
        }
    }

    func cancelAll() async {
        let identifiers = await center.pendingNotificationRequests()
            .map(\.identifier)
            .filter { $0.hasPrefix(ReadingReminderPlanner.identifierPrefix) }
        center.removePendingNotificationRequests(withIdentifiers: identifiers)
    }

    func nextReminderDate() async -> Date? {
        await center.pendingNotificationRequests()
            .filter { $0.identifier.hasPrefix(ReadingReminderPlanner.identifierPrefix) }
            .compactMap { ($0.trigger as? UNCalendarNotificationTrigger)?.nextTriggerDate() }
            .min()
    }
}

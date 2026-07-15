import XCTest
@testable import Yomora

final class ReadingReminderPlannerTests: XCTestCase {
    func testPlansAtMostFortyTwoStableDailyReminders() throws {
        let calendar = utcCalendar()
        let now = try XCTUnwrap(calendar.date(from: DateComponents(year: 2026, month: 7, day: 15, hour: 10)))
        let preferences = ReadingReminderPreferences(enabled: true, minutesAfterMidnight: 20 * 60, weekdayMask: 0b1111111)

        let reminders = ReadingReminderPlanner.plan(
            preferences: preferences,
            now: now,
            skippingToday: false,
            calendar: calendar
        )

        XCTAssertEqual(reminders.count, 42)
        XCTAssertEqual(reminders.first?.date, calendar.date(from: DateComponents(year: 2026, month: 7, day: 15, hour: 20)))
        XCTAssertEqual(reminders.first?.identifier, "app.yomora.reading-reminder.2026-07-15")
        XCTAssertEqual(Set(reminders.map(\.identifier)).count, 42)
    }

    func testSkipsTodayAfterGoalIsMetAndHonorsSelectedWeekdays() throws {
        let calendar = utcCalendar()
        let now = try XCTUnwrap(calendar.date(from: DateComponents(year: 2026, month: 7, day: 15, hour: 10)))
        let mondayAndWednesday = (1 << (2 - 1)) | (1 << (4 - 1))
        let preferences = ReadingReminderPreferences(
            enabled: true,
            minutesAfterMidnight: 20 * 60,
            weekdayMask: mondayAndWednesday
        )

        let reminders = ReadingReminderPlanner.plan(
            preferences: preferences,
            now: now,
            skippingToday: true,
            calendar: calendar
        )

        XCTAssertEqual(reminders.first?.date, calendar.date(from: DateComponents(year: 2026, month: 7, day: 20, hour: 20)))
        XCTAssertFalse(reminders.contains { calendar.isDate($0.date, inSameDayAs: now) })
        XCTAssertTrue(reminders.allSatisfy { [2, 4].contains(calendar.component(.weekday, from: $0.date)) })
    }

    private func utcCalendar() -> Calendar {
        var calendar = Calendar(identifier: .gregorian)
        calendar.timeZone = TimeZone(secondsFromGMT: 0)!
        calendar.locale = Locale(identifier: "pt_BR")
        return calendar
    }
}

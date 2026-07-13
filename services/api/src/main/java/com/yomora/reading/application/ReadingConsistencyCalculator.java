package com.yomora.reading.application;

import com.yomora.reading.domain.ReadingGoal;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ReadingConsistencyCalculator {
    private static final int WINDOW_DAYS = 30;

    public ConsistencySummary calculate(
            ReadingGoal goal,
            Map<LocalDate, DailyReading> readings,
            LocalDate today
    ) {
        LocalDate since = today.minusDays(WINDOW_DAYS - 1L);
        Set<LocalDate> fulfilled = new HashSet<>();
        int daysRead = 0;

        for (Map.Entry<LocalDate, DailyReading> entry : readings.entrySet()) {
            if (entry.getKey().isBefore(since) || entry.getKey().isAfter(today)) {
                continue;
            }
            DailyReading reading = entry.getValue();
            if (reading.minutes() > 0 || reading.pages() > 0) {
                daysRead++;
            }
            boolean minutesMet = reading.minutes() >= goal.dailyMinutes();
            boolean pagesMet = goal.dailyPages() == null || reading.pages() >= goal.dailyPages();
            if (minutesMet && pagesMet) {
                fulfilled.add(entry.getKey());
            }
        }

        int currentStreak = streakEndingAt(fulfilled, fulfilled.contains(today) ? today : today.minusDays(1));
        int bestStreak = bestStreak(fulfilled, since, today);
        int plannedDays = Math.max(1, (int) Math.ceil(WINDOW_DAYS * goal.weeklyDays() / 7.0));
        double pace = round2(Math.min(100.0, fulfilled.size() * 100.0 / plannedDays));
        return new ConsistencySummary(currentStreak, bestStreak, daysRead, pace);
    }

    private int streakEndingAt(Set<LocalDate> fulfilled, LocalDate end) {
        int streak = 0;
        LocalDate cursor = end;
        while (fulfilled.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private int bestStreak(Set<LocalDate> fulfilled, LocalDate since, LocalDate today) {
        int current = 0;
        int best = 0;
        for (LocalDate cursor = since; !cursor.isAfter(today); cursor = cursor.plusDays(1)) {
            if (fulfilled.contains(cursor)) {
                current++;
                best = Math.max(best, current);
            } else {
                current = 0;
            }
        }
        return best;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

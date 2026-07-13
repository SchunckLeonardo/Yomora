package com.yomora.reading.application;

import java.util.Map;

public record StatisticsSummary(
        long minutesToday,
        long minutesThisWeek,
        long pagesThisWeek,
        int daysReadLast30,
        int currentStreak,
        int bestStreak,
        long finishedBooksThisYear,
        Map<String, Long> topGenres,
        long totalSessions,
        double pacePercent
) {
}

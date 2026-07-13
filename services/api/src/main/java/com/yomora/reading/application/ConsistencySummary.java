package com.yomora.reading.application;

public record ConsistencySummary(
        int currentStreak,
        int bestStreak,
        int daysReadLast30,
        double pacePercent
) {
}

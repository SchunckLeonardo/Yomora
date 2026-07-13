package com.yomora.reading.application;

import java.time.LocalDate;
import java.util.UUID;

public record ReadingSessionSummary(
        UUID sessionId,
        long durationMinutes,
        int pagesRead,
        double progressPercent,
        double averagePagesPerHour,
        int estimatedSessionsRemaining,
        LocalDate estimatedFinishDate,
        int currentStreak
) {
}

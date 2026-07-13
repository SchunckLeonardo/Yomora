package com.yomora.identity.application;

public record ProfileMetrics(
        long followers,
        long following,
        long finishedBooks,
        long totalReadingMinutes,
        int currentStreak
) {
}

package com.yomora.reading.domain;

import java.time.Instant;
import java.util.UUID;

public record ReadingSession(
        UUID id,
        UUID userId,
        UUID userBookId,
        int startPage,
        int currentPage,
        Integer endPage,
        Integer goalPages,
        Instant startedAt,
        Instant pausedAt,
        long pausedSeconds,
        Instant finishedAt,
        Long durationSeconds,
        Integer pagesRead,
        String note
) {
    public boolean active() {
        return finishedAt == null;
    }

    public boolean paused() {
        return pausedAt != null;
    }
}

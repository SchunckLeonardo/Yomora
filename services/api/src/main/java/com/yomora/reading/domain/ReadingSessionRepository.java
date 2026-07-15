package com.yomora.reading.domain;

import com.yomora.reading.application.DailyReading;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ReadingSessionRepository {
    ReadingSession save(ReadingSession session);

    Optional<ReadingSession> findOwned(UUID id, UUID userId);

    Optional<ReadingSession> findActive(UUID userId);

    List<ReadingSession> list(UUID userId);

    Map<LocalDate, DailyReading> dailyReading(UUID userId, LocalDate since);
}

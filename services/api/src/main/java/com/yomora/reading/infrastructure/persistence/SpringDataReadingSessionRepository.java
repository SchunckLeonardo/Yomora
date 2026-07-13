package com.yomora.reading.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataReadingSessionRepository extends JpaRepository<ReadingSessionEntity, UUID> {
    Optional<ReadingSessionEntity> findByIdAndUserId(UUID id, UUID userId);

    List<ReadingSessionEntity> findAllByUserIdOrderByStartedAtDesc(UUID userId);

    @Query(value = """
            SELECT (finished_at AT TIME ZONE 'UTC')::date AS readingDate,
                   COALESCE(SUM(duration_seconds) / 60, 0) AS minutes,
                   COALESCE(SUM(pages_read), 0) AS pages
            FROM reading_sessions
            WHERE user_id = :userId
              AND finished_at IS NOT NULL
              AND (finished_at AT TIME ZONE 'UTC')::date >= :since
            GROUP BY (finished_at AT TIME ZONE 'UTC')::date
            """, nativeQuery = true)
    List<DailyReadingProjection> dailyReading(@Param("userId") UUID userId, @Param("since") LocalDate since);
}

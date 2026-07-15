package com.yomora.reading.infrastructure.persistence;

import com.yomora.reading.application.DailyReading;
import com.yomora.reading.domain.ReadingSession;
import com.yomora.reading.domain.ReadingSessionRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaReadingSessionRepository implements ReadingSessionRepository {
    private final SpringDataReadingSessionRepository repository;

    JpaReadingSessionRepository(SpringDataReadingSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public ReadingSession save(ReadingSession session) {
        return repository.save(ReadingSessionEntity.from(session)).toDomain();
    }

    @Override
    public Optional<ReadingSession> findOwned(UUID id, UUID userId) {
        return repository.findByIdAndUserId(id, userId).map(ReadingSessionEntity::toDomain);
    }

    @Override
    public Optional<ReadingSession> findActive(UUID userId) {
        return repository.findFirstByUserIdAndFinishedAtIsNullOrderByStartedAtDesc(userId)
                .map(ReadingSessionEntity::toDomain);
    }

    @Override
    public List<ReadingSession> list(UUID userId) {
        return repository.findAllByUserIdOrderByStartedAtDesc(userId).stream()
                .map(ReadingSessionEntity::toDomain)
                .toList();
    }

    @Override
    public Map<LocalDate, DailyReading> dailyReading(UUID userId, LocalDate since) {
        Map<LocalDate, DailyReading> result = new LinkedHashMap<>();
        repository.dailyReading(userId, since).forEach(row -> result.put(
                row.getReadingDate(), new DailyReading(row.getMinutes(), row.getPages())
        ));
        return result;
    }
}

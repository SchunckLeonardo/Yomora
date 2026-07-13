package com.yomora.reading.infrastructure.persistence;

import com.yomora.reading.domain.ReadingGoal;
import com.yomora.reading.domain.ReadingGoalRepository;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaReadingGoalRepository implements ReadingGoalRepository {
    private final SpringDataReadingGoalRepository repository;
    private final Clock clock;

    JpaReadingGoalRepository(SpringDataReadingGoalRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public ReadingGoal save(UUID userId, ReadingGoal goal) {
        Instant now = clock.instant();
        Instant createdAt = repository.findById(userId).map(entity -> entity.createdAt).orElse(now);
        return repository.save(new ReadingGoalEntity(userId, goal, createdAt, now)).toDomain();
    }

    @Override
    public Optional<ReadingGoal> findByUserId(UUID userId) {
        return repository.findById(userId).map(ReadingGoalEntity::toDomain);
    }
}

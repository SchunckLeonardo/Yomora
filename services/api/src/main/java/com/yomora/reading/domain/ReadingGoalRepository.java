package com.yomora.reading.domain;

import java.util.Optional;
import java.util.UUID;

public interface ReadingGoalRepository {
    ReadingGoal save(UUID userId, ReadingGoal goal);

    Optional<ReadingGoal> findByUserId(UUID userId);
}

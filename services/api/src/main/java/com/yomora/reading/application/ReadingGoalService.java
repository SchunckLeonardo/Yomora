package com.yomora.reading.application;

import com.yomora.reading.domain.ReadingGoal;
import com.yomora.reading.domain.ReadingGoalRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class ReadingGoalService {
    private static final ReadingGoal DEFAULT_GOAL = new ReadingGoal(20, 5, null);
    private final ReadingGoalRepository repository;

    public ReadingGoalService(ReadingGoalRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public ReadingGoal get(UUID userId) {
        return repository.findByUserId(userId).orElse(DEFAULT_GOAL);
    }

    @Transactional
    public ReadingGoal update(UUID userId, ReadingGoal goal) {
        return repository.save(userId, goal);
    }
}

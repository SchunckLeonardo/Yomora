package com.yomora.reading.infrastructure.persistence;

import com.yomora.reading.domain.ReadingGoal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reading_goals")
class ReadingGoalEntity {
    @Id
    @Column(name = "user_id")
    UUID userId;
    @Column(name = "daily_minutes")
    int dailyMinutes;
    @Column(name = "weekly_days")
    int weeklyDays;
    @Column(name = "daily_pages")
    Integer dailyPages;
    @Column(name = "created_at")
    Instant createdAt;
    @Column(name = "updated_at")
    Instant updatedAt;

    protected ReadingGoalEntity() {
    }

    ReadingGoalEntity(UUID userId, ReadingGoal goal, Instant createdAt, Instant updatedAt) {
        this.userId = userId;
        this.dailyMinutes = goal.dailyMinutes();
        this.weeklyDays = goal.weeklyDays();
        this.dailyPages = goal.dailyPages();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    ReadingGoal toDomain() {
        return new ReadingGoal(dailyMinutes, weeklyDays, dailyPages);
    }
}

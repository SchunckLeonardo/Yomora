package com.yomora.reading.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SpringDataReadingGoalRepository extends JpaRepository<ReadingGoalEntity, UUID> {
}

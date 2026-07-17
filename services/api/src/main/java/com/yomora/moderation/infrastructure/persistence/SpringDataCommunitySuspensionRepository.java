package com.yomora.moderation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

interface SpringDataCommunitySuspensionRepository extends JpaRepository<CommunitySuspensionEntity, UUID> {
    Optional<CommunitySuspensionEntity> findFirstByUserIdAndReversedAtIsNullAndEndsAtAfterOrderByEndsAtDesc(
            UUID userId,
            Instant now
    );
}

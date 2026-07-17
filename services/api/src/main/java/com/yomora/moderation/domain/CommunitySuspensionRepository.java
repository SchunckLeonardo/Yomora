package com.yomora.moderation.domain;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface CommunitySuspensionRepository {
    CommunitySuspension save(CommunitySuspension value);

    Optional<CommunitySuspension> findById(UUID id);

    Optional<CommunitySuspension> findActiveByUserId(UUID userId, Instant now);
}

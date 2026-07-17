package com.yomora.moderation.domain;

import java.time.Instant;
import java.util.UUID;

public record CommunitySuspension(
        UUID id,
        UUID userId,
        String reason,
        Instant startsAt,
        Instant endsAt,
        UUID createdBy,
        Instant reversedAt,
        UUID reversedBy,
        Instant createdAt
) {
    public CommunitySuspension reverse(UUID administratorId, Instant reversedAt) {
        return new CommunitySuspension(
                id, userId, reason, startsAt, endsAt, createdBy,
                reversedAt, administratorId, createdAt
        );
    }
}

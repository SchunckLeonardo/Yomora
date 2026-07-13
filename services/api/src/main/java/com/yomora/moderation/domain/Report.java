package com.yomora.moderation.domain;

import java.time.Instant;
import java.util.UUID;

public record Report(
        UUID id,
        UUID reporterId,
        UUID reportedUserId,
        UUID postId,
        String reason,
        String details,
        String status,
        Instant createdAt
) {
}

package com.yomora.review.domain;

import java.time.Instant;
import java.util.UUID;

public record Review(
        UUID id,
        UUID userId,
        UUID workId,
        UUID editionId,
        int rating,
        String title,
        String text,
        boolean spoiler,
        UUID postId,
        Instant createdAt,
        Instant updatedAt
) {
}

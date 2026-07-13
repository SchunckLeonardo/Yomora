package com.yomora.review.domain;

import java.time.Instant;
import java.util.UUID;

public record Note(
        UUID id,
        UUID userId,
        UUID editionId,
        String content,
        Integer page,
        String chapter,
        boolean privateNote,
        boolean spoiler,
        UUID postId,
        Instant createdAt,
        Instant updatedAt
) {
}

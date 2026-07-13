package com.yomora.social.domain;

import java.time.Instant;
import java.util.UUID;

public record Post(
        UUID id,
        UUID authorId,
        String text,
        UUID editionId,
        PostType type,
        boolean spoiler,
        Integer spoilerPage,
        Visibility visibility,
        Instant createdAt,
        Instant updatedAt,
        long likeCount,
        long commentCount
) {
    public Post withEngagement(long likes, long comments) {
        return new Post(
                id, authorId, text, editionId, type, spoiler, spoilerPage, visibility,
                createdAt, updatedAt, likes, comments
        );
    }
}

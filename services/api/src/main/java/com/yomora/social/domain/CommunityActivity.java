package com.yomora.social.domain;

import java.time.Instant;
import java.util.UUID;

public record CommunityActivity(
        UUID id,
        UUID recipientId,
        UUID actorId,
        ActivityType type,
        UUID postId,
        boolean read,
        Instant createdAt
) {
    public CommunityActivity markRead() {
        return new CommunityActivity(id, recipientId, actorId, type, postId, true, createdAt);
    }
}

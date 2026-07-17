package com.yomora.social.domain;

import java.time.Instant;
import java.util.UUID;

public record FollowRequest(
        UUID followerId,
        UUID followedId,
        FollowStatus status,
        Instant createdAt
) {
}

package com.yomora.identity.domain;

import java.time.Instant;
import java.util.UUID;

public record RefreshToken(
        UUID id,
        UUID userId,
        String tokenHash,
        Instant expiresAt,
        Instant revokedAt,
        Instant createdAt
) {
    public boolean activeAt(Instant instant) {
        return revokedAt == null && expiresAt.isAfter(instant);
    }

    public RefreshToken revokeAt(Instant instant) {
        return new RefreshToken(id, userId, tokenHash, expiresAt, instant, createdAt);
    }
}

package com.yomora.identity.domain;

import java.time.Instant;
import java.util.UUID;

public record PasswordResetToken(
        UUID id,
        UUID userId,
        String tokenHash,
        Instant expiresAt,
        Instant usedAt,
        Instant createdAt
) {
    public boolean activeAt(Instant instant) {
        return usedAt == null && expiresAt.isAfter(instant);
    }

    public PasswordResetToken useAt(Instant instant) {
        return new PasswordResetToken(id, userId, tokenHash, expiresAt, instant, createdAt);
    }
}

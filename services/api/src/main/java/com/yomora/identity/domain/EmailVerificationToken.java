package com.yomora.identity.domain;

import java.time.Instant;
import java.util.UUID;

public record EmailVerificationToken(
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

    public EmailVerificationToken useAt(Instant instant) {
        return new EmailVerificationToken(id, userId, tokenHash, expiresAt, instant, createdAt);
    }
}

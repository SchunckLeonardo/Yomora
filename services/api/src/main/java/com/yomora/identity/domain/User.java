package com.yomora.identity.domain;

import java.time.Instant;
import java.util.UUID;

public record User(
        UUID id,
        String name,
        String username,
        String email,
        String passwordHash,
        Instant emailVerifiedAt,
        Instant profileCompletedAt,
        String bio,
        String avatarUrl,
        boolean publicProfile,
        Instant createdAt,
        Instant updatedAt
) {
    public boolean hasPassword() {
        return passwordHash != null && !passwordHash.isBlank();
    }

    public boolean emailVerified() {
        return emailVerifiedAt != null;
    }

    public boolean profileComplete() {
        return profileCompletedAt != null;
    }

    public User verifyEmailAt(Instant verifiedAt) {
        return new User(id, name, username, email, passwordHash, verifiedAt, profileCompletedAt,
                bio, avatarUrl, publicProfile, createdAt, verifiedAt);
    }
}

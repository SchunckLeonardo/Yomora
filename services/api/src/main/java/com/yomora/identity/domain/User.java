package com.yomora.identity.domain;

import java.time.Instant;
import java.util.UUID;

public record User(
        UUID id,
        String name,
        String username,
        String email,
        String passwordHash,
        String bio,
        String avatarUrl,
        boolean publicProfile,
        Instant createdAt,
        Instant updatedAt
) {
}

package com.yomora.identity.domain;

import java.time.Instant;
import java.util.UUID;

public record ExternalIdentity(
        UUID id,
        UUID userId,
        ExternalProvider provider,
        String subject,
        String email,
        String encryptedRefreshToken,
        Instant createdAt,
        Instant updatedAt
) {
}

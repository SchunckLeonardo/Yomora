package com.yomora.identity.domain;

import java.util.Optional;
import java.time.Instant;
import java.util.UUID;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken token);

    Optional<RefreshToken> findByHash(String tokenHash);

    void revokeAllByUserId(UUID userId, Instant revokedAt);
}

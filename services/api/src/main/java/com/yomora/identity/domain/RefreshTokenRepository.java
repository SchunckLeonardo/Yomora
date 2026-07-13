package com.yomora.identity.domain;

import java.util.Optional;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken token);

    Optional<RefreshToken> findByHash(String tokenHash);
}

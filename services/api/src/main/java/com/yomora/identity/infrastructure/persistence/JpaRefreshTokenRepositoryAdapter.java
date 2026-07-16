package com.yomora.identity.infrastructure.persistence;

import com.yomora.identity.domain.RefreshToken;
import com.yomora.identity.domain.RefreshTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.Instant;
import java.util.UUID;

@Repository
class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepository {
    private final SpringDataRefreshTokenRepository repository;

    JpaRefreshTokenRepositoryAdapter(SpringDataRefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        return repository.save(RefreshTokenEntity.from(token)).toDomain();
    }

    @Override
    public Optional<RefreshToken> findByHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash).map(RefreshTokenEntity::toDomain);
    }

    @Override
    public void revokeAllByUserId(UUID userId, Instant revokedAt) {
        repository.revokeAllByUserId(userId, revokedAt);
    }
}

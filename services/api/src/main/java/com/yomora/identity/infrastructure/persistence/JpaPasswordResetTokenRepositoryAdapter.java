package com.yomora.identity.infrastructure.persistence;

import com.yomora.identity.domain.PasswordResetToken;
import com.yomora.identity.domain.PasswordResetTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class JpaPasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepository {
    private final SpringDataPasswordResetTokenRepository repository;

    JpaPasswordResetTokenRepositoryAdapter(SpringDataPasswordResetTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        return repository.save(PasswordResetTokenEntity.from(token)).toDomain();
    }

    @Override
    public Optional<PasswordResetToken> findByHash(String hash) {
        return repository.findByTokenHash(hash).map(PasswordResetTokenEntity::toDomain);
    }
}

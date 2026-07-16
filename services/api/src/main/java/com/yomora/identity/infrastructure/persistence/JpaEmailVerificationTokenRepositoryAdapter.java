package com.yomora.identity.infrastructure.persistence;

import com.yomora.identity.domain.EmailVerificationToken;
import com.yomora.identity.domain.EmailVerificationTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class JpaEmailVerificationTokenRepositoryAdapter implements EmailVerificationTokenRepository {
    private final SpringDataEmailVerificationTokenRepository repository;

    JpaEmailVerificationTokenRepositoryAdapter(SpringDataEmailVerificationTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public EmailVerificationToken save(EmailVerificationToken token) {
        return repository.save(EmailVerificationTokenEntity.from(token)).toDomain();
    }

    @Override
    public Optional<EmailVerificationToken> findByHash(String hash) {
        return repository.findByTokenHash(hash).map(EmailVerificationTokenEntity::toDomain);
    }
}

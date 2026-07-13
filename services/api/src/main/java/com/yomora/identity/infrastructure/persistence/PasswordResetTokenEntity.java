package com.yomora.identity.infrastructure.persistence;

import com.yomora.identity.domain.PasswordResetToken;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
class PasswordResetTokenEntity {
    @Id UUID id;
    @Column(name = "user_id") UUID userId;
    @Column(name = "token_hash") String tokenHash;
    @Column(name = "expires_at") Instant expiresAt;
    @Column(name = "used_at") Instant usedAt;
    @Column(name = "created_at") Instant createdAt;

    protected PasswordResetTokenEntity() {
    }

    private PasswordResetTokenEntity(PasswordResetToken token) {
        id = token.id(); userId = token.userId(); tokenHash = token.tokenHash();
        expiresAt = token.expiresAt(); usedAt = token.usedAt(); createdAt = token.createdAt();
    }

    static PasswordResetTokenEntity from(PasswordResetToken token) { return new PasswordResetTokenEntity(token); }

    PasswordResetToken toDomain() {
        return new PasswordResetToken(id, userId, tokenHash, expiresAt, usedAt, createdAt);
    }
}

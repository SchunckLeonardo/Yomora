package com.yomora.identity.infrastructure.persistence;

import com.yomora.identity.domain.RefreshToken;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
class RefreshTokenEntity {
    @Id
    UUID id;
    @Column(name = "user_id")
    UUID userId;
    @Column(name = "token_hash")
    String tokenHash;
    @Column(name = "expires_at")
    Instant expiresAt;
    @Column(name = "revoked_at")
    Instant revokedAt;
    @Column(name = "created_at")
    Instant createdAt;

    protected RefreshTokenEntity() {
    }

    private RefreshTokenEntity(RefreshToken token) {
        this.id = token.id();
        this.userId = token.userId();
        this.tokenHash = token.tokenHash();
        this.expiresAt = token.expiresAt();
        this.revokedAt = token.revokedAt();
        this.createdAt = token.createdAt();
    }

    static RefreshTokenEntity from(RefreshToken token) {
        return new RefreshTokenEntity(token);
    }

    RefreshToken toDomain() {
        return new RefreshToken(id, userId, tokenHash, expiresAt, revokedAt, createdAt);
    }
}

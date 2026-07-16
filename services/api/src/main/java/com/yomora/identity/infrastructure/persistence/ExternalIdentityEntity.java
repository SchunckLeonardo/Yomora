package com.yomora.identity.infrastructure.persistence;

import com.yomora.identity.domain.ExternalIdentity;
import com.yomora.identity.domain.ExternalProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "external_identities")
class ExternalIdentityEntity {
    @Id UUID id;
    @Column(name = "user_id") UUID userId;
    @Enumerated(EnumType.STRING) ExternalProvider provider;
    String subject;
    String email;
    @Column(name = "encrypted_refresh_token") String encryptedRefreshToken;
    @Column(name = "created_at") Instant createdAt;
    @Column(name = "updated_at") Instant updatedAt;

    protected ExternalIdentityEntity() {
    }

    private ExternalIdentityEntity(ExternalIdentity identity) {
        id = identity.id();
        userId = identity.userId();
        provider = identity.provider();
        subject = identity.subject();
        email = identity.email();
        encryptedRefreshToken = identity.encryptedRefreshToken();
        createdAt = identity.createdAt();
        updatedAt = identity.updatedAt();
    }

    static ExternalIdentityEntity from(ExternalIdentity identity) {
        return new ExternalIdentityEntity(identity);
    }

    ExternalIdentity toDomain() {
        return new ExternalIdentity(
                id, userId, provider, subject, email, encryptedRefreshToken, createdAt, updatedAt
        );
    }
}

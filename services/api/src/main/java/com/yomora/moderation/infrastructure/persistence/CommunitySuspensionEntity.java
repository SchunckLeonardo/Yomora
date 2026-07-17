package com.yomora.moderation.infrastructure.persistence;

import com.yomora.moderation.domain.CommunitySuspension;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "community_suspensions")
class CommunitySuspensionEntity {
    @Id UUID id;
    @Column(name = "user_id") UUID userId;
    String reason;
    @Column(name = "starts_at") Instant startsAt;
    @Column(name = "ends_at") Instant endsAt;
    @Column(name = "created_by") UUID createdBy;
    @Column(name = "reversed_at") Instant reversedAt;
    @Column(name = "reversed_by") UUID reversedBy;
    @Column(name = "created_at") Instant createdAt;

    protected CommunitySuspensionEntity() {
    }

    private CommunitySuspensionEntity(CommunitySuspension value) {
        id = value.id();
        userId = value.userId();
        reason = value.reason();
        startsAt = value.startsAt();
        endsAt = value.endsAt();
        createdBy = value.createdBy();
        reversedAt = value.reversedAt();
        reversedBy = value.reversedBy();
        createdAt = value.createdAt();
    }

    static CommunitySuspensionEntity from(CommunitySuspension value) {
        return new CommunitySuspensionEntity(value);
    }

    CommunitySuspension toDomain() {
        return new CommunitySuspension(
                id, userId, reason, startsAt, endsAt, createdBy,
                reversedAt, reversedBy, createdAt
        );
    }
}

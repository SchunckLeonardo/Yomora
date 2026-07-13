package com.yomora.moderation.infrastructure.persistence;

import com.yomora.moderation.domain.BlockedUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "blocked_users")
class BlockedUserEntity {
    @Id
    UUID id;
    @Column(name = "blocker_id")
    UUID blockerId;
    @Column(name = "blocked_id")
    UUID blockedId;
    @Column(name = "created_at")
    Instant createdAt;

    protected BlockedUserEntity() {
    }

    private BlockedUserEntity(BlockedUser value) {
        this.id = value.id();
        this.blockerId = value.blockerId();
        this.blockedId = value.blockedId();
        this.createdAt = value.createdAt();
    }

    static BlockedUserEntity from(BlockedUser value) {
        return new BlockedUserEntity(value);
    }

    BlockedUser toDomain() {
        return new BlockedUser(id, blockerId, blockedId, createdAt);
    }
}

package com.yomora.social.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "post_likes")
class LikeEntity {
    @Id
    UUID id;
    @Column(name = "post_id")
    UUID postId;
    @Column(name = "user_id")
    UUID userId;
    @Column(name = "created_at")
    Instant createdAt;

    protected LikeEntity() {
    }

    LikeEntity(UUID id, UUID postId, UUID userId, Instant createdAt) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.createdAt = createdAt;
    }
}

package com.yomora.social.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "follows")
class FollowEntity {
    @Id
    UUID id;
    @Column(name = "follower_id")
    UUID followerId;
    @Column(name = "followed_id")
    UUID followedId;
    String status;
    @Column(name = "created_at")
    Instant createdAt;

    protected FollowEntity() {
    }

    FollowEntity(UUID id, UUID followerId, UUID followedId, String status, Instant createdAt) {
        this.id = id;
        this.followerId = followerId;
        this.followedId = followedId;
        this.status = status;
        this.createdAt = createdAt;
    }
}

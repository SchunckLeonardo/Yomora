package com.yomora.social.infrastructure.persistence;

import com.yomora.social.domain.ActivityType;
import com.yomora.social.domain.CommunityActivity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "community_activities")
class CommunityActivityEntity {
    @Id
    UUID id;
    @Column(name = "recipient_id")
    UUID recipientId;
    @Column(name = "actor_id")
    UUID actorId;
    String type;
    @Column(name = "post_id")
    UUID postId;
    boolean read;
    @Column(name = "created_at")
    Instant createdAt;

    protected CommunityActivityEntity() {
    }

    private CommunityActivityEntity(CommunityActivity activity) {
        id = activity.id();
        recipientId = activity.recipientId();
        actorId = activity.actorId();
        type = activity.type().name();
        postId = activity.postId();
        read = activity.read();
        createdAt = activity.createdAt();
    }

    static CommunityActivityEntity from(CommunityActivity activity) {
        return new CommunityActivityEntity(activity);
    }

    CommunityActivity toDomain() {
        return new CommunityActivity(
                id, recipientId, actorId, ActivityType.valueOf(type), postId, read, createdAt
        );
    }
}

package com.yomora.social.application;

import java.util.UUID;

public interface ActivityPublisher {
    void postLiked(UUID recipientId, UUID actorId, UUID postId);

    void postCommented(UUID recipientId, UUID actorId, UUID postId);

    void followRequested(UUID recipientId, UUID actorId);

    void followAccepted(UUID recipientId, UUID actorId);

    void followed(UUID recipientId, UUID actorId);

    static ActivityPublisher noOp() {
        return new ActivityPublisher() {
            @Override public void postLiked(UUID recipientId, UUID actorId, UUID postId) { }
            @Override public void postCommented(UUID recipientId, UUID actorId, UUID postId) { }
            @Override public void followRequested(UUID recipientId, UUID actorId) { }
            @Override public void followAccepted(UUID recipientId, UUID actorId) { }
            @Override public void followed(UUID recipientId, UUID actorId) { }
        };
    }
}

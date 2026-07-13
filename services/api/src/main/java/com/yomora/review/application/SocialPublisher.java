package com.yomora.review.application;

import com.yomora.social.domain.PostType;
import com.yomora.social.domain.Visibility;

import java.util.UUID;

public interface SocialPublisher {
    UUID upsert(
            UUID authorId,
            UUID existingPostId,
            UUID editionId,
            String text,
            PostType type,
            boolean spoiler,
            Visibility visibility
    );

    void delete(UUID authorId, UUID postId);
}

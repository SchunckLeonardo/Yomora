package com.yomora.review.infrastructure.social;

import com.yomora.review.application.SocialPublisher;
import com.yomora.social.application.SocialService;
import com.yomora.social.domain.Post;
import com.yomora.social.domain.PostType;
import com.yomora.social.domain.Visibility;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class SocialPublisherAdapter implements SocialPublisher {
    private final SocialService socialService;

    SocialPublisherAdapter(SocialService socialService) {
        this.socialService = socialService;
    }

    @Override
    public UUID upsert(
            UUID authorId,
            UUID existingPostId,
            UUID editionId,
            String text,
            PostType type,
            boolean spoiler,
            Visibility visibility
    ) {
        Post post = existingPostId == null
                ? socialService.createPost(authorId, text, editionId, type, spoiler, null, visibility)
                : socialService.editPost(authorId, existingPostId, text, spoiler, null, visibility);
        return post.id();
    }

    @Override
    public void delete(UUID authorId, UUID postId) {
        socialService.deletePost(authorId, postId);
    }
}

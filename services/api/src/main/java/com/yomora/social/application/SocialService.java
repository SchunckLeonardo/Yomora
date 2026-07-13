package com.yomora.social.application;

import com.yomora.social.domain.Comment;
import com.yomora.social.domain.Post;
import com.yomora.social.domain.PostType;
import com.yomora.social.domain.SocialRepository;
import com.yomora.social.domain.Visibility;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class SocialService {
    private final SocialRepository repository;
    private final Clock clock;

    public SocialService(SocialRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public Post createPost(
            UUID authorId,
            String text,
            UUID editionId,
            PostType type,
            boolean spoiler,
            Integer spoilerPage,
            Visibility visibility
    ) {
        Instant now = clock.instant();
        return repository.savePost(new Post(
                UUID.randomUUID(), authorId, normalizeText(text), editionId,
                type, spoiler, spoilerPage, visibility, now, now, 0, 0
        ));
    }

    @Transactional(readOnly = true)
    public Post getPost(UUID postId) {
        return repository.findPost(postId).orElseThrow(ContentNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Post getPostFor(UUID viewerId, UUID postId) {
        Post post = getPost(postId);
        if (post.visibility() == Visibility.PRIVATE && !post.authorId().equals(viewerId)) {
            throw new ContentForbiddenException();
        }
        if (post.visibility() == Visibility.FOLLOWERS
                && !post.authorId().equals(viewerId)
                && !repository.followers(post.authorId()).contains(viewerId)) {
            throw new ContentForbiddenException();
        }
        return post;
    }

    @Transactional
    public Post editPost(
            UUID authorId,
            UUID postId,
            String text,
            boolean spoiler,
            Integer spoilerPage,
            Visibility visibility
    ) {
        Post current = getPost(postId);
        requireOwner(current.authorId(), authorId);
        return repository.savePost(new Post(
                current.id(), current.authorId(), normalizeText(text), current.editionId(), current.type(),
                spoiler, spoilerPage, visibility, current.createdAt(), clock.instant(),
                current.likeCount(), current.commentCount()
        ));
    }

    @Transactional
    public void deletePost(UUID authorId, UUID postId) {
        Post current = getPost(postId);
        requireOwner(current.authorId(), authorId);
        repository.deletePost(postId);
    }

    @Transactional
    public Post like(UUID userId, UUID postId) {
        getPost(postId);
        repository.setLike(postId, userId, true);
        return getPost(postId);
    }

    @Transactional
    public Post unlike(UUID userId, UUID postId) {
        getPost(postId);
        repository.setLike(postId, userId, false);
        return getPost(postId);
    }

    @Transactional
    public Comment comment(UUID authorId, UUID postId, String text) {
        getPost(postId);
        return repository.saveComment(new Comment(
                UUID.randomUUID(), postId, authorId, normalizeText(text), clock.instant()
        ));
    }

    @Transactional
    public void deleteComment(UUID authorId, UUID commentId) {
        Comment comment = repository.findComment(commentId).orElseThrow(ContentNotFoundException::new);
        requireOwner(comment.authorId(), authorId);
        repository.deleteComment(commentId);
    }

    @Transactional(readOnly = true)
    public List<Comment> comments(UUID postId) {
        getPost(postId);
        return repository.comments(postId);
    }

    @Transactional
    public void follow(UUID followerId, UUID followedId) {
        if (followerId.equals(followedId)) {
            throw new InvalidFollowException("Você não pode seguir a si mesmo");
        }
        repository.setFollowing(followerId, followedId, true);
    }

    @Transactional
    public void unfollow(UUID followerId, UUID followedId) {
        repository.setFollowing(followerId, followedId, false);
    }

    @Transactional(readOnly = true)
    public List<UUID> followers(UUID userId) {
        return repository.followers(userId);
    }

    @Transactional(readOnly = true)
    public List<UUID> following(UUID userId) {
        return repository.following(userId);
    }

    @Transactional(readOnly = true)
    public List<Post> followingFeed(UUID userId, Instant cursor, int limit) {
        return repository.followingFeed(userId, cursor, bounded(limit));
    }

    @Transactional(readOnly = true)
    public List<Post> discover(Instant cursor, int limit) {
        return repository.discover(cursor, bounded(limit));
    }

    private void requireOwner(UUID ownerId, UUID actorId) {
        if (!ownerId.equals(actorId)) {
            throw new ContentForbiddenException();
        }
    }

    private String normalizeText(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("O texto não pode ser vazio");
        }
        return text.trim();
    }

    private int bounded(int limit) {
        return Math.max(1, Math.min(limit, 50));
    }
}

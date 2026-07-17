package com.yomora.social.application;

import com.yomora.social.domain.Comment;
import com.yomora.social.domain.FollowRequest;
import com.yomora.social.domain.FollowStatus;
import com.yomora.social.domain.Post;
import com.yomora.social.domain.PostType;
import com.yomora.social.domain.SocialRepository;
import com.yomora.social.domain.Visibility;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SocialService {
    private final SocialRepository repository;
    private final ProfileVisibility profileVisibility;
    private final SocialBlockPolicy blockPolicy;
    private final Clock clock;

    public SocialService(SocialRepository repository, Clock clock) {
        this(repository, ignored -> true, (first, second) -> false, clock);
    }

    public SocialService(
            SocialRepository repository,
            ProfileVisibility profileVisibility,
            SocialBlockPolicy blockPolicy,
            Clock clock
    ) {
        this.repository = repository;
        this.profileVisibility = profileVisibility;
        this.blockPolicy = blockPolicy;
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
        requireNotBlocked(viewerId, post.authorId());
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
        getPostFor(userId, postId);
        repository.setLike(postId, userId, true);
        return getPostFor(userId, postId);
    }

    @Transactional
    public Post unlike(UUID userId, UUID postId) {
        getPostFor(userId, postId);
        repository.setLike(postId, userId, false);
        return getPostFor(userId, postId);
    }

    @Transactional
    public Comment comment(UUID authorId, UUID postId, String text) {
        getPostFor(authorId, postId);
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
    public List<Comment> comments(UUID viewerId, UUID postId) {
        getPostFor(viewerId, postId);
        return repository.comments(postId);
    }

    @Transactional
    public FollowStatus follow(UUID followerId, UUID followedId) {
        if (followerId.equals(followedId)) {
            throw new InvalidFollowException("Você não pode seguir a si mesmo");
        }
        requireNotBlocked(followerId, followedId);
        FollowStatus current = repository.followStatus(followerId, followedId).orElse(null);
        if (current == FollowStatus.ACCEPTED) {
            return current;
        }
        FollowStatus requested = profileVisibility.isPublic(followedId)
                ? FollowStatus.ACCEPTED
                : FollowStatus.PENDING;
        return repository.setFollowing(followerId, followedId, requested);
    }

    @Transactional
    public void unfollow(UUID followerId, UUID followedId) {
        repository.removeFollowing(followerId, followedId);
    }

    @Transactional(readOnly = true)
    public Optional<FollowStatus> followStatus(UUID followerId, UUID followedId) {
        if (blockPolicy.isBlockedEitherWay(followerId, followedId)) {
            return Optional.empty();
        }
        return repository.followStatus(followerId, followedId);
    }

    @Transactional
    public void approveFollow(UUID followedId, UUID followerId) {
        requireNotBlocked(followerId, followedId);
        FollowStatus status = repository.followStatus(followerId, followedId)
                .orElseThrow(() -> new InvalidFollowException("Solicitação para seguir não encontrada"));
        if (status != FollowStatus.PENDING) {
            throw new InvalidFollowException("A solicitação para seguir já foi processada");
        }
        repository.setFollowing(followerId, followedId, FollowStatus.ACCEPTED);
    }

    @Transactional
    public void rejectFollow(UUID followedId, UUID followerId) {
        if (repository.followStatus(followerId, followedId).orElse(null) == FollowStatus.PENDING) {
            repository.removeFollowing(followerId, followedId);
        }
    }

    @Transactional(readOnly = true)
    public List<FollowRequest> pendingFollowRequests(UUID followedId) {
        return repository.pendingFollowRequests(followedId).stream()
                .filter(request -> !blockPolicy.isBlockedEitherWay(request.followerId(), followedId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UUID> followers(UUID userId) {
        return repository.followers(userId).stream()
                .filter(followerId -> !blockPolicy.isBlockedEitherWay(followerId, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UUID> followers(UUID viewerId, UUID userId) {
        requireNotBlocked(viewerId, userId);
        return followers(userId);
    }

    @Transactional(readOnly = true)
    public List<UUID> following(UUID userId) {
        return repository.following(userId).stream()
                .filter(followedId -> !blockPolicy.isBlockedEitherWay(userId, followedId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UUID> following(UUID viewerId, UUID userId) {
        requireNotBlocked(viewerId, userId);
        return following(userId);
    }

    @Transactional(readOnly = true)
    public List<Post> followingFeed(UUID userId, Instant cursor, int limit) {
        return repository.followingFeed(userId, cursor, bounded(limit)).stream()
                .filter(post -> !blockPolicy.isBlockedEitherWay(userId, post.authorId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Post> discover(UUID viewerId, Instant cursor, int limit) {
        return repository.discover(viewerId, cursor, bounded(limit)).stream()
                .filter(post -> !blockPolicy.isBlockedEitherWay(viewerId, post.authorId()))
                .toList();
    }

    private void requireOwner(UUID ownerId, UUID actorId) {
        if (!ownerId.equals(actorId)) {
            throw new ContentForbiddenException();
        }
    }

    private void requireNotBlocked(UUID firstUserId, UUID secondUserId) {
        if (!firstUserId.equals(secondUserId) && blockPolicy.isBlockedEitherWay(firstUserId, secondUserId)) {
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

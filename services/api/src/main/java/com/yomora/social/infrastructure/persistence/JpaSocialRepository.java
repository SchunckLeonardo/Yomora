package com.yomora.social.infrastructure.persistence;

import com.yomora.social.domain.Comment;
import com.yomora.social.domain.Post;
import com.yomora.social.domain.SocialRepository;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaSocialRepository implements SocialRepository {
    private final SpringDataPostRepository postRepository;
    private final SpringDataLikeRepository likeRepository;
    private final SpringDataCommentRepository commentRepository;
    private final SpringDataFollowRepository followRepository;
    private final Clock clock;

    JpaSocialRepository(
            SpringDataPostRepository postRepository,
            SpringDataLikeRepository likeRepository,
            SpringDataCommentRepository commentRepository,
            SpringDataFollowRepository followRepository,
            Clock clock
    ) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.followRepository = followRepository;
        this.clock = clock;
    }

    @Override
    public Post savePost(Post post) {
        postRepository.save(PostEntity.from(post));
        return enriched(post.id());
    }

    @Override
    public Optional<Post> findPost(UUID postId) {
        return postRepository.findById(postId).map(entity -> entity.toDomain(
                likeRepository.countByPostId(postId), commentRepository.countByPostId(postId)
        ));
    }

    @Override
    public void deletePost(UUID postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public void setLike(UUID postId, UUID userId, boolean liked) {
        Optional<LikeEntity> existing = likeRepository.findByPostIdAndUserId(postId, userId);
        if (liked && existing.isEmpty()) {
            likeRepository.save(new LikeEntity(UUID.randomUUID(), postId, userId, clock.instant()));
        }
        if (!liked) {
            existing.ifPresent(likeRepository::delete);
        }
    }

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(CommentEntity.from(comment)).toDomain();
    }

    @Override
    public Optional<Comment> findComment(UUID commentId) {
        return commentRepository.findById(commentId).map(CommentEntity::toDomain);
    }

    @Override
    public void deleteComment(UUID commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<Comment> comments(UUID postId) {
        return commentRepository.findAllByPostIdOrderByCreatedAt(postId).stream()
                .map(CommentEntity::toDomain)
                .toList();
    }

    @Override
    public void setFollowing(UUID followerId, UUID followedId, boolean following) {
        Optional<FollowEntity> existing = followRepository.findByFollowerIdAndFollowedId(followerId, followedId);
        if (following && existing.isEmpty()) {
            followRepository.save(new FollowEntity(
                    UUID.randomUUID(), followerId, followedId, "ACCEPTED", clock.instant()
            ));
        }
        if (!following) {
            existing.ifPresent(followRepository::delete);
        }
    }

    @Override
    public List<UUID> followers(UUID userId) {
        return followRepository.findAllByFollowedIdAndStatus(userId, "ACCEPTED").stream()
                .map(entity -> entity.followerId)
                .toList();
    }

    @Override
    public List<UUID> following(UUID userId) {
        return followRepository.findAllByFollowerIdAndStatus(userId, "ACCEPTED").stream()
                .map(entity -> entity.followedId)
                .toList();
    }

    @Override
    public List<Post> followingFeed(UUID userId, Instant cursor, int limit) {
        return postRepository.followingFeed(userId, cursor, limit).stream().map(this::enriched).toList();
    }

    @Override
    public List<Post> discover(Instant cursor, int limit) {
        return postRepository.discover(cursor, limit).stream().map(this::enriched).toList();
    }

    private Post enriched(PostEntity entity) {
        return entity.toDomain(likeRepository.countByPostId(entity.id), commentRepository.countByPostId(entity.id));
    }

    private Post enriched(UUID postId) {
        return findPost(postId).orElseThrow();
    }
}

package com.yomora.social.application;

import com.yomora.social.domain.Comment;
import com.yomora.social.domain.FollowRequest;
import com.yomora.social.domain.FollowStatus;
import com.yomora.social.domain.Post;
import com.yomora.social.domain.PostType;
import com.yomora.social.domain.SocialRepository;
import com.yomora.social.domain.Visibility;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SocialServiceTest {
    @Test
    void enforcesOwnershipAndKeepsLikesIdempotent() {
        UUID author = UUID.randomUUID();
        UUID reader = UUID.randomUUID();
        InMemorySocialRepository repository = new InMemorySocialRepository();
        SocialService service = new SocialService(
                repository,
                Clock.fixed(Instant.parse("2026-07-13T18:00:00Z"), ZoneOffset.UTC)
        );

        Post post = service.createPost(
                author, "Leiam este livro.", null, PostType.RECOMMENDATION,
                false, null, Visibility.PUBLIC
        );
        service.like(reader, post.id());
        service.like(reader, post.id());
        Comment comment = service.comment(reader, post.id(), "Entrou na lista!");

        assertThat(service.getPost(post.id()).likeCount()).isEqualTo(1);
        assertThat(service.getPost(post.id()).commentCount()).isEqualTo(1);
        assertThatThrownBy(() -> service.editPost(
                reader, post.id(), "texto adulterado", false, null, Visibility.PUBLIC
        )).isInstanceOf(ContentForbiddenException.class);
        assertThatThrownBy(() -> service.deleteComment(author, comment.id()))
                .isInstanceOf(ContentForbiddenException.class);
    }

    @Test
    void rejectsFollowingYourself() {
        UUID user = UUID.randomUUID();
        SocialService service = new SocialService(
                new InMemorySocialRepository(),
                Clock.fixed(Instant.parse("2026-07-13T18:00:00Z"), ZoneOffset.UTC)
        );

        assertThatThrownBy(() -> service.follow(user, user))
                .isInstanceOf(InvalidFollowException.class)
                .hasMessage("Você não pode seguir a si mesmo");
    }

    @Test
    void enforcesPostAudienceBeforeLikesAndComments() {
        UUID author = UUID.randomUUID();
        UUID outsider = UUID.randomUUID();
        SocialService service = new SocialService(
                new InMemorySocialRepository(),
                Clock.fixed(Instant.parse("2026-07-13T18:00:00Z"), ZoneOffset.UTC)
        );
        Post followersOnly = service.createPost(
                author, "Somente para seguidores.", null, PostType.NOTE,
                false, null, Visibility.FOLLOWERS
        );

        assertThatThrownBy(() -> service.like(outsider, followersOnly.id()))
                .isInstanceOf(ContentForbiddenException.class);
        assertThatThrownBy(() -> service.comment(outsider, followersOnly.id(), "Não deveria entrar"))
                .isInstanceOf(ContentForbiddenException.class);
    }

    @Test
    void keepsPrivateProfileFollowPendingUntilOwnerApprovesIt() {
        UUID follower = UUID.randomUUID();
        UUID privateProfile = UUID.randomUUID();
        InMemorySocialRepository repository = new InMemorySocialRepository();
        SocialService service = new SocialService(
                repository,
                userId -> !userId.equals(privateProfile),
                (firstUserId, secondUserId) -> false,
                Clock.fixed(Instant.parse("2026-07-13T18:00:00Z"), ZoneOffset.UTC)
        );

        assertThat(service.follow(follower, privateProfile)).isEqualTo(FollowStatus.PENDING);
        assertThat(service.followers(privateProfile)).isEmpty();
        assertThat(service.pendingFollowRequests(privateProfile))
                .extracting(FollowRequest::followerId)
                .containsExactly(follower);

        service.approveFollow(privateProfile, follower);

        assertThat(service.followers(privateProfile)).containsExactly(follower);
        assertThat(service.pendingFollowRequests(privateProfile)).isEmpty();
    }

    @Test
    void mutualBlockDeniesProfilesPostsAndFollowAttempts() {
        UUID author = UUID.randomUUID();
        UUID blockedReader = UUID.randomUUID();
        InMemorySocialRepository repository = new InMemorySocialRepository();
        SocialService service = new SocialService(
                repository,
                ignored -> true,
                (firstUserId, secondUserId) -> Set.of(firstUserId, secondUserId)
                        .equals(Set.of(author, blockedReader)),
                Clock.fixed(Instant.parse("2026-07-13T18:00:00Z"), ZoneOffset.UTC)
        );
        Post post = service.createPost(
                author, "Publicação pública", null, PostType.NOTE,
                false, null, Visibility.PUBLIC
        );

        assertThatThrownBy(() -> service.getPostFor(blockedReader, post.id()))
                .isInstanceOf(ContentForbiddenException.class);
        assertThatThrownBy(() -> service.follow(blockedReader, author))
                .isInstanceOf(ContentForbiddenException.class);
        assertThatThrownBy(() -> service.followers(blockedReader, author))
                .isInstanceOf(ContentForbiddenException.class);
    }

    private static final class InMemorySocialRepository implements SocialRepository {
        private final Map<UUID, Post> posts = new HashMap<>();
        private final Map<UUID, Comment> comments = new HashMap<>();
        private final Map<UUID, Set<UUID>> likes = new HashMap<>();
        private final Map<String, FollowRequest> follows = new HashMap<>();

        @Override
        public Post savePost(Post post) {
            posts.put(post.id(), post);
            return post;
        }

        @Override
        public Optional<Post> findPost(UUID postId) {
            Post post = posts.get(postId);
            if (post == null) {
                return Optional.empty();
            }
            long likeCount = likes.getOrDefault(postId, Set.of()).size();
            long commentCount = comments.values().stream().filter(comment -> comment.postId().equals(postId)).count();
            return Optional.of(post.withEngagement(likeCount, commentCount));
        }

        @Override
        public void deletePost(UUID postId) {
            posts.remove(postId);
        }

        @Override
        public void setLike(UUID postId, UUID userId, boolean liked) {
            Set<UUID> postLikes = likes.computeIfAbsent(postId, ignored -> new HashSet<>());
            if (liked) {
                postLikes.add(userId);
            } else {
                postLikes.remove(userId);
            }
        }

        @Override
        public Comment saveComment(Comment comment) {
            comments.put(comment.id(), comment);
            return comment;
        }

        @Override
        public Optional<Comment> findComment(UUID commentId) {
            return Optional.ofNullable(comments.get(commentId));
        }

        @Override
        public void deleteComment(UUID commentId) {
            comments.remove(commentId);
        }

        @Override
        public List<Comment> comments(UUID postId) {
            return comments.values().stream().filter(comment -> comment.postId().equals(postId)).toList();
        }

        @Override
        public FollowStatus setFollowing(UUID followerId, UUID followedId, FollowStatus status) {
            String key = followerId + ":" + followedId;
            FollowRequest current = follows.get(key);
            Instant createdAt = current == null ? Instant.parse("2026-07-13T18:00:00Z") : current.createdAt();
            follows.put(key, new FollowRequest(followerId, followedId, status, createdAt));
            return status;
        }

        @Override
        public Optional<FollowStatus> followStatus(UUID followerId, UUID followedId) {
            return Optional.ofNullable(follows.get(followerId + ":" + followedId)).map(FollowRequest::status);
        }

        @Override
        public void removeFollowing(UUID followerId, UUID followedId) {
            follows.remove(followerId + ":" + followedId);
        }

        @Override
        public void removeConnectionsBetween(UUID firstUserId, UUID secondUserId) {
            removeFollowing(firstUserId, secondUserId);
            removeFollowing(secondUserId, firstUserId);
        }

        @Override
        public List<FollowRequest> pendingFollowRequests(UUID followedId) {
            return follows.values().stream()
                    .filter(request -> request.followedId().equals(followedId))
                    .filter(request -> request.status() == FollowStatus.PENDING)
                    .toList();
        }

        @Override
        public List<UUID> followers(UUID userId) {
            return follows.values().stream()
                    .filter(request -> request.followedId().equals(userId))
                    .filter(request -> request.status() == FollowStatus.ACCEPTED)
                    .map(FollowRequest::followerId)
                    .toList();
        }

        @Override
        public List<UUID> following(UUID userId) {
            return follows.values().stream()
                    .filter(request -> request.followerId().equals(userId))
                    .filter(request -> request.status() == FollowStatus.ACCEPTED)
                    .map(FollowRequest::followedId)
                    .toList();
        }

        @Override
        public List<Post> followingFeed(UUID userId, Instant cursor, int limit) {
            return new ArrayList<>(posts.values());
        }

        @Override
        public List<Post> discover(UUID viewerId, Instant cursor, int limit) {
            return new ArrayList<>(posts.values());
        }
    }
}

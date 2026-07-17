package com.yomora.social.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SocialRepository {
    Post savePost(Post post);

    Optional<Post> findPost(UUID postId);

    void deletePost(UUID postId);

    boolean setLike(UUID postId, UUID userId, boolean liked);

    Comment saveComment(Comment comment);

    Optional<Comment> findComment(UUID commentId);

    void deleteComment(UUID commentId);

    List<Comment> comments(UUID postId);

    FollowStatus setFollowing(UUID followerId, UUID followedId, FollowStatus status);

    Optional<FollowStatus> followStatus(UUID followerId, UUID followedId);

    void removeFollowing(UUID followerId, UUID followedId);

    void removeConnectionsBetween(UUID firstUserId, UUID secondUserId);

    List<FollowRequest> pendingFollowRequests(UUID followedId);

    List<UUID> followers(UUID userId);

    List<UUID> following(UUID userId);

    List<Post> followingFeed(UUID userId, Instant cursor, int limit);

    List<Post> discover(UUID viewerId, Instant cursor, int limit);
}

package com.yomora.social.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

interface SpringDataPostRepository extends JpaRepository<PostEntity, UUID> {
    @Query(value = """
            SELECT p.* FROM posts p
            JOIN follows f ON f.followed_id = p.author_id
            WHERE f.follower_id = :userId
              AND f.status = 'ACCEPTED'
              AND p.visibility IN ('PUBLIC', 'FOLLOWERS')
              AND (CAST(:cursor AS TIMESTAMPTZ) IS NULL OR p.created_at < CAST(:cursor AS TIMESTAMPTZ))
            ORDER BY p.created_at DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<PostEntity> followingFeed(
            @Param("userId") UUID userId,
            @Param("cursor") Instant cursor,
            @Param("limit") int limit
    );

    @Query(value = """
            SELECT p.* FROM posts p
            LEFT JOIN post_likes likes ON likes.post_id = p.id
            LEFT JOIN comments comments ON comments.post_id = p.id
            WHERE p.visibility = 'PUBLIC'
              AND (CAST(:cursor AS TIMESTAMPTZ) IS NULL OR p.created_at < CAST(:cursor AS TIMESTAMPTZ))
            GROUP BY p.id
            ORDER BY (COUNT(DISTINCT likes.id) * 3 + COUNT(DISTINCT comments.id) * 2) DESC,
                     p.created_at DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<PostEntity> discover(@Param("cursor") Instant cursor, @Param("limit") int limit);
}

package com.yomora.social.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataLikeRepository extends JpaRepository<LikeEntity, UUID> {
    Optional<LikeEntity> findByPostIdAndUserId(UUID postId, UUID userId);

    long countByPostId(UUID postId);
}

package com.yomora.social.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface SpringDataCommentRepository extends JpaRepository<CommentEntity, UUID> {
    long countByPostId(UUID postId);

    List<CommentEntity> findAllByPostIdOrderByCreatedAt(UUID postId);
}

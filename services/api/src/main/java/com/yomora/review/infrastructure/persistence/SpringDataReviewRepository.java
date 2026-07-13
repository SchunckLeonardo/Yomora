package com.yomora.review.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataReviewRepository extends JpaRepository<ReviewEntity, UUID> {
    Optional<ReviewEntity> findByUserIdAndWorkId(UUID userId, UUID workId);

    Optional<ReviewEntity> findByIdAndUserId(UUID id, UUID userId);

    List<ReviewEntity> findAllByWorkIdOrderByCreatedAtDesc(UUID workId);
}

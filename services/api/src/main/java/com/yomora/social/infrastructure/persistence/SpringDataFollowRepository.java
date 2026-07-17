package com.yomora.social.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataFollowRepository extends JpaRepository<FollowEntity, UUID> {
    Optional<FollowEntity> findByFollowerIdAndFollowedId(UUID followerId, UUID followedId);

    List<FollowEntity> findAllByFollowedIdAndStatus(UUID followedId, String status);

    List<FollowEntity> findAllByFollowerIdAndStatus(UUID followerId, String status);

    void deleteByFollowerIdAndFollowedId(UUID followerId, UUID followedId);
}

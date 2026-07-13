package com.yomora.moderation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataBlockedUserRepository extends JpaRepository<BlockedUserEntity, UUID> {
    Optional<BlockedUserEntity> findByBlockerIdAndBlockedId(UUID blockerId, UUID blockedId);
}

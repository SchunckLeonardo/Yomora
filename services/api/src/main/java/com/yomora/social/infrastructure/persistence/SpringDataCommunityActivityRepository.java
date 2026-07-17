package com.yomora.social.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface SpringDataCommunityActivityRepository extends JpaRepository<CommunityActivityEntity, UUID> {
    List<CommunityActivityEntity> findAllByRecipientIdOrderByCreatedAtDesc(UUID recipientId, Pageable pageable);
}

package com.yomora.identity.infrastructure.persistence;

import com.yomora.identity.domain.ExternalProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

interface SpringDataExternalIdentityRepository extends JpaRepository<ExternalIdentityEntity, UUID> {
    Optional<ExternalIdentityEntity> findByProviderAndSubject(ExternalProvider provider, String subject);

    Optional<ExternalIdentityEntity> findByUserIdAndProvider(UUID userId, ExternalProvider provider);

    List<ExternalIdentityEntity> findAllByUserId(UUID userId);
}

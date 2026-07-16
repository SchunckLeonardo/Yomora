package com.yomora.identity.domain;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface ExternalIdentityRepository {
    ExternalIdentity save(ExternalIdentity identity);

    Optional<ExternalIdentity> findByProviderAndSubject(ExternalProvider provider, String subject);

    Optional<ExternalIdentity> findByUserIdAndProvider(UUID userId, ExternalProvider provider);

    List<ExternalIdentity> findAllByUserId(UUID userId);

    void delete(ExternalIdentity identity);
}

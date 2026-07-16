package com.yomora.identity.infrastructure.persistence;

import com.yomora.identity.domain.ExternalIdentity;
import com.yomora.identity.domain.ExternalIdentityRepository;
import com.yomora.identity.domain.ExternalProvider;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
class JpaExternalIdentityRepositoryAdapter implements ExternalIdentityRepository {
    private final SpringDataExternalIdentityRepository repository;

    JpaExternalIdentityRepositoryAdapter(SpringDataExternalIdentityRepository repository) {
        this.repository = repository;
    }

    @Override
    public ExternalIdentity save(ExternalIdentity identity) {
        return repository.save(ExternalIdentityEntity.from(identity)).toDomain();
    }

    @Override
    public Optional<ExternalIdentity> findByProviderAndSubject(ExternalProvider provider, String subject) {
        return repository.findByProviderAndSubject(provider, subject).map(ExternalIdentityEntity::toDomain);
    }

    @Override
    public Optional<ExternalIdentity> findByUserIdAndProvider(UUID userId, ExternalProvider provider) {
        return repository.findByUserIdAndProvider(userId, provider).map(ExternalIdentityEntity::toDomain);
    }

    @Override
    public List<ExternalIdentity> findAllByUserId(UUID userId) {
        return repository.findAllByUserId(userId).stream().map(ExternalIdentityEntity::toDomain).toList();
    }

    @Override
    public void delete(ExternalIdentity identity) {
        repository.deleteById(identity.id());
    }
}

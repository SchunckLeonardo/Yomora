package com.yomora.moderation.infrastructure.persistence;

import com.yomora.moderation.domain.CommunitySuspension;
import com.yomora.moderation.domain.CommunitySuspensionRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaCommunitySuspensionRepository implements CommunitySuspensionRepository {
    private final SpringDataCommunitySuspensionRepository repository;

    JpaCommunitySuspensionRepository(SpringDataCommunitySuspensionRepository repository) {
        this.repository = repository;
    }

    @Override
    public CommunitySuspension save(CommunitySuspension value) {
        return repository.save(CommunitySuspensionEntity.from(value)).toDomain();
    }

    @Override
    public Optional<CommunitySuspension> findById(UUID id) {
        return repository.findById(id).map(CommunitySuspensionEntity::toDomain);
    }

    @Override
    public Optional<CommunitySuspension> findActiveByUserId(UUID userId, Instant now) {
        return repository.findFirstByUserIdAndReversedAtIsNullAndEndsAtAfterOrderByEndsAtDesc(userId, now)
                .map(CommunitySuspensionEntity::toDomain);
    }
}

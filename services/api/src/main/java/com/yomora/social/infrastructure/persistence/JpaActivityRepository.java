package com.yomora.social.infrastructure.persistence;

import com.yomora.social.domain.ActivityRepository;
import com.yomora.social.domain.CommunityActivity;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaActivityRepository implements ActivityRepository {
    private final SpringDataCommunityActivityRepository repository;

    JpaActivityRepository(SpringDataCommunityActivityRepository repository) {
        this.repository = repository;
    }

    @Override
    public CommunityActivity save(CommunityActivity activity) {
        return repository.save(CommunityActivityEntity.from(activity)).toDomain();
    }

    @Override
    public Optional<CommunityActivity> findById(UUID activityId) {
        return repository.findById(activityId).map(CommunityActivityEntity::toDomain);
    }

    @Override
    public List<CommunityActivity> list(UUID recipientId, int limit) {
        return repository.findAllByRecipientIdOrderByCreatedAtDesc(recipientId, PageRequest.of(0, limit)).stream()
                .map(CommunityActivityEntity::toDomain)
                .toList();
    }
}

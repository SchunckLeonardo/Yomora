package com.yomora.social.application;

import com.yomora.social.domain.ActivityRepository;
import com.yomora.social.domain.ActivityType;
import com.yomora.social.domain.CommunityActivity;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

public class CommunityActivityService implements ActivityPublisher {
    private final ActivityRepository repository;
    private final Clock clock;

    public CommunityActivityService(ActivityRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void postLiked(UUID recipientId, UUID actorId, UUID postId) {
        publish(recipientId, actorId, ActivityType.POST_LIKED, postId);
    }

    @Override
    @Transactional
    public void postCommented(UUID recipientId, UUID actorId, UUID postId) {
        publish(recipientId, actorId, ActivityType.POST_COMMENTED, postId);
    }

    @Override
    @Transactional
    public void followRequested(UUID recipientId, UUID actorId) {
        publish(recipientId, actorId, ActivityType.FOLLOW_REQUEST, null);
    }

    @Override
    @Transactional
    public void followAccepted(UUID recipientId, UUID actorId) {
        publish(recipientId, actorId, ActivityType.FOLLOW_ACCEPTED, null);
    }

    @Override
    @Transactional
    public void followed(UUID recipientId, UUID actorId) {
        publish(recipientId, actorId, ActivityType.USER_FOLLOWED, null);
    }

    @Transactional(readOnly = true)
    public List<CommunityActivity> list(UUID recipientId) {
        return repository.list(recipientId, 100);
    }

    @Transactional
    public CommunityActivity markRead(UUID recipientId, UUID activityId) {
        CommunityActivity activity = repository.findById(activityId)
                .orElseThrow(ContentNotFoundException::new);
        if (!activity.recipientId().equals(recipientId)) {
            throw new ContentForbiddenException();
        }
        return repository.save(activity.markRead());
    }

    private void publish(UUID recipientId, UUID actorId, ActivityType type, UUID postId) {
        if (recipientId.equals(actorId)) {
            return;
        }
        repository.save(new CommunityActivity(
                UUID.randomUUID(), recipientId, actorId, type, postId, false, clock.instant()
        ));
    }
}

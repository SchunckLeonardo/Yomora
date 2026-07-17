package com.yomora.social.application;

import com.yomora.social.domain.ActivityRepository;
import com.yomora.social.domain.CommunityActivity;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CommunityActivityServiceTest {
    @Test
    void listsNewestFirstMarksReadAndSkipsSelfInteractions() {
        UUID recipient = UUID.randomUUID();
        UUID actor = UUID.randomUUID();
        InMemoryActivityRepository repository = new InMemoryActivityRepository();
        CommunityActivityService service = new CommunityActivityService(
                repository,
                Clock.fixed(Instant.parse("2026-07-13T18:00:00Z"), ZoneOffset.UTC)
        );

        service.postLiked(recipient, recipient, UUID.randomUUID());
        service.postCommented(recipient, actor, UUID.randomUUID());

        assertThat(service.list(recipient)).hasSize(1);
        CommunityActivity activity = service.list(recipient).getFirst();
        assertThat(activity.read()).isFalse();

        CommunityActivity read = service.markRead(recipient, activity.id());

        assertThat(read.read()).isTrue();
    }

    private static final class InMemoryActivityRepository implements ActivityRepository {
        private final List<CommunityActivity> activities = new ArrayList<>();

        @Override
        public CommunityActivity save(CommunityActivity activity) {
            activities.removeIf(existing -> existing.id().equals(activity.id()));
            activities.add(activity);
            return activity;
        }

        @Override
        public Optional<CommunityActivity> findById(UUID activityId) {
            return activities.stream().filter(activity -> activity.id().equals(activityId)).findFirst();
        }

        @Override
        public List<CommunityActivity> list(UUID recipientId, int limit) {
            return activities.stream()
                    .filter(activity -> activity.recipientId().equals(recipientId))
                    .sorted((first, second) -> second.createdAt().compareTo(first.createdAt()))
                    .limit(limit)
                    .toList();
        }
    }
}

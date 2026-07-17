package com.yomora.moderation.application;

import com.yomora.moderation.domain.BlockedUser;
import com.yomora.moderation.domain.ModerationRepository;
import com.yomora.moderation.domain.Report;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModerationServiceTest {
    @Test
    void validatesTargetsAndKeepsBlockingIdempotent() {
        UUID user = UUID.randomUUID();
        UUID blocked = UUID.randomUUID();
        InMemoryModerationRepository repository = new InMemoryModerationRepository();
        ModerationService service = new ModerationService(
                repository,
                Clock.fixed(Instant.parse("2026-07-13T18:00:00Z"), ZoneOffset.UTC)
        );

        BlockedUser first = service.block(user, blocked);
        BlockedUser repeated = service.block(user, blocked);

        assertThat(repeated.id()).isEqualTo(first.id());
        assertThatThrownBy(() -> service.block(user, user)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.report(user, null, null, "SPAM", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void blockingRemovesFollowConnectionsInBothDirections() {
        UUID blocker = UUID.randomUUID();
        UUID blocked = UUID.randomUUID();
        List<String> removedConnections = new ArrayList<>();
        ModerationService service = new ModerationService(
                new InMemoryModerationRepository(),
                (firstUserId, secondUserId) -> removedConnections.add(firstUserId + ":" + secondUserId),
                Clock.fixed(Instant.parse("2026-07-13T18:00:00Z"), ZoneOffset.UTC)
        );

        service.block(blocker, blocked);

        assertThat(removedConnections).containsExactly(blocker + ":" + blocked);
    }

    private static final class InMemoryModerationRepository implements ModerationRepository {
        private final Map<String, BlockedUser> blocked = new HashMap<>();

        @Override
        public Optional<BlockedUser> findBlock(UUID blockerId, UUID blockedId) {
            return Optional.ofNullable(blocked.get(blockerId + ":" + blockedId));
        }

        @Override
        public BlockedUser saveBlock(BlockedUser value) {
            blocked.put(value.blockerId() + ":" + value.blockedId(), value);
            return value;
        }

        @Override
        public void deleteBlock(BlockedUser value) {
            blocked.remove(value.blockerId() + ":" + value.blockedId());
        }

        @Override
        public Report saveReport(Report report) {
            return report;
        }

        @Override
        public Optional<Report> findReportById(UUID reportId) {
            return Optional.empty();
        }

        @Override
        public List<Report> findReportsByStatus(String status) {
            return List.of();
        }
    }
}

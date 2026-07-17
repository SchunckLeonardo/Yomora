package com.yomora.moderation.application;

import com.yomora.moderation.domain.CommunitySuspension;
import com.yomora.moderation.domain.CommunitySuspensionRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommunitySuspensionServiceTest {
    private static final Instant NOW = Instant.parse("2026-07-17T12:00:00Z");
    private final UUID adminId = UUID.randomUUID();
    private final InMemorySuspensionRepository repository = new InMemorySuspensionRepository();
    private final CommunitySuspensionService service = new CommunitySuspensionService(
            repository,
            new AdminAccessPolicy(Set.of(adminId)),
            Clock.fixed(NOW, ZoneOffset.UTC)
    );

    @Test
    void suspendsCommunityAccessForOneSevenOrThirtyDays() {
        UUID userId = UUID.randomUUID();

        CommunitySuspension suspension = service.suspend(adminId, userId, 7, "Assédio");

        assertThat(suspension.endsAt()).isEqualTo(NOW.plusSeconds(7 * 86_400L));
        assertThat(service.accessFor(userId).allowed()).isFalse();
        assertThat(service.accessFor(userId).suspendedUntil()).isEqualTo(suspension.endsAt());
        assertThatThrownBy(() -> service.suspend(adminId, userId, 1, "Outro motivo"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("suspensão ativa");
        assertThatThrownBy(() -> service.suspend(adminId, userId, 2, "Motivo"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1, 7 ou 30");
    }

    @Test
    void rejectsNonAdministratorsAndSupportsManualReversal() {
        UUID userId = UUID.randomUUID();
        CommunitySuspension suspension = service.suspend(adminId, userId, 1, "Spam");

        assertThatThrownBy(() -> service.suspend(UUID.randomUUID(), userId, 1, "Spam"))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);

        service.reverse(adminId, suspension.id());

        assertThat(service.accessFor(userId).allowed()).isTrue();
    }

    private static final class InMemorySuspensionRepository implements CommunitySuspensionRepository {
        private final List<CommunitySuspension> values = new ArrayList<>();

        @Override
        public CommunitySuspension save(CommunitySuspension value) {
            values.removeIf(existing -> existing.id().equals(value.id()));
            values.add(value);
            return value;
        }

        @Override
        public Optional<CommunitySuspension> findById(UUID id) {
            return values.stream().filter(value -> value.id().equals(id)).findFirst();
        }

        @Override
        public Optional<CommunitySuspension> findActiveByUserId(UUID userId, Instant now) {
            return values.stream()
                    .filter(value -> value.userId().equals(userId))
                    .filter(value -> value.reversedAt() == null && value.endsAt().isAfter(now))
                    .findFirst();
        }
    }
}

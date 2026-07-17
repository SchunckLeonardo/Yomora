package com.yomora.moderation.application;

import com.yomora.moderation.domain.CommunitySuspension;
import com.yomora.moderation.domain.CommunitySuspensionRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class CommunitySuspensionService {
    private static final Set<Integer> ALLOWED_DURATIONS = Set.of(1, 7, 30);

    private final CommunitySuspensionRepository repository;
    private final AdminAccessPolicy adminAccess;
    private final Clock clock;

    public CommunitySuspensionService(
            CommunitySuspensionRepository repository,
            AdminAccessPolicy adminAccess,
            Clock clock
    ) {
        this.repository = repository;
        this.adminAccess = adminAccess;
        this.clock = clock;
    }

    @Transactional
    public CommunitySuspension suspend(UUID administratorId, UUID userId, int days, String reason) {
        adminAccess.requireAdmin(administratorId);
        if (!ALLOWED_DURATIONS.contains(days)) {
            throw new IllegalArgumentException("A suspensão deve durar 1, 7 ou 30 dias");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("O motivo da suspensão é obrigatório");
        }
        Instant now = clock.instant();
        if (repository.findActiveByUserId(userId, now).isPresent()) {
            throw new IllegalArgumentException("O usuário já possui uma suspensão ativa");
        }
        return repository.save(new CommunitySuspension(
                UUID.randomUUID(), userId, reason.trim(), now, now.plus(Duration.ofDays(days)),
                administratorId, null, null, now
        ));
    }

    @Transactional
    public CommunitySuspension reverse(UUID administratorId, UUID suspensionId) {
        adminAccess.requireAdmin(administratorId);
        CommunitySuspension suspension = repository.findById(suspensionId)
                .orElseThrow(() -> new IllegalArgumentException("Suspensão não encontrada"));
        if (suspension.reversedAt() != null) {
            return suspension;
        }
        return repository.save(suspension.reverse(administratorId, clock.instant()));
    }

    @Transactional(readOnly = true)
    public CommunityAccess accessFor(UUID userId) {
        return repository.findActiveByUserId(userId, clock.instant())
                .map(value -> new CommunityAccess(false, value.endsAt(), value.reason()))
                .orElseGet(CommunityAccess::allowedAccess);
    }
}

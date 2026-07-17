package com.yomora.moderation.application;

import com.yomora.moderation.domain.BlockedUser;
import com.yomora.moderation.domain.ModerationRepository;
import com.yomora.moderation.domain.Report;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

public class ModerationService {
    private final ModerationRepository repository;
    private final SocialConnectionRemover connectionRemover;
    private final Clock clock;

    public ModerationService(ModerationRepository repository, Clock clock) {
        this(repository, (first, second) -> { }, clock);
    }

    public ModerationService(
            ModerationRepository repository,
            SocialConnectionRemover connectionRemover,
            Clock clock
    ) {
        this.repository = repository;
        this.connectionRemover = connectionRemover;
        this.clock = clock;
    }

    @Transactional
    public BlockedUser block(UUID blockerId, UUID blockedId) {
        if (blockerId.equals(blockedId)) {
            throw new IllegalArgumentException("Você não pode bloquear a si mesmo");
        }
        BlockedUser block = repository.findBlock(blockerId, blockedId).orElseGet(() -> repository.saveBlock(
                new BlockedUser(UUID.randomUUID(), blockerId, blockedId, clock.instant())
        ));
        connectionRemover.removeBetween(blockerId, blockedId);
        return block;
    }

    @Transactional
    public void unblock(UUID blockerId, UUID blockedId) {
        repository.findBlock(blockerId, blockedId).ifPresent(repository::deleteBlock);
    }

    @Transactional
    public Report report(
            UUID reporterId,
            UUID reportedUserId,
            UUID postId,
            String reason,
            String details
    ) {
        if (reportedUserId == null && postId == null) {
            throw new IllegalArgumentException("A denúncia precisa indicar um usuário ou publicação");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("O motivo da denúncia é obrigatório");
        }
        return repository.saveReport(new Report(
                UUID.randomUUID(), reporterId, reportedUserId, postId, reason.trim(),
                details == null || details.isBlank() ? null : details.trim(), "OPEN", clock.instant()
        ));
    }
}

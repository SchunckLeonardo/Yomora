package com.yomora.moderation.domain;

import java.util.Optional;
import java.util.UUID;

public interface ModerationRepository {
    Optional<BlockedUser> findBlock(UUID blockerId, UUID blockedId);

    BlockedUser saveBlock(BlockedUser value);

    void deleteBlock(BlockedUser value);

    Report saveReport(Report report);
}

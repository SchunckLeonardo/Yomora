package com.yomora.moderation.domain;

import java.util.Optional;
import java.util.UUID;

public interface ModerationRepository {
    Optional<BlockedUser> findBlock(UUID blockerId, UUID blockedId);

    default boolean isBlockedEitherWay(UUID firstUserId, UUID secondUserId) {
        return findBlock(firstUserId, secondUserId).isPresent()
                || findBlock(secondUserId, firstUserId).isPresent();
    }

    BlockedUser saveBlock(BlockedUser value);

    void deleteBlock(BlockedUser value);

    Report saveReport(Report report);
}

package com.yomora.moderation.application;

import java.util.UUID;

@FunctionalInterface
public interface SocialConnectionRemover {
    void removeBetween(UUID firstUserId, UUID secondUserId);
}

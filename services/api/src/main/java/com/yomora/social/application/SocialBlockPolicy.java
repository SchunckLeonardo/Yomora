package com.yomora.social.application;

import java.util.UUID;

@FunctionalInterface
public interface SocialBlockPolicy {
    boolean isBlockedEitherWay(UUID firstUserId, UUID secondUserId);
}

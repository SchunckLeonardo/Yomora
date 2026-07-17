package com.yomora.social.application;

import java.util.UUID;

@FunctionalInterface
public interface ProfileVisibility {
    boolean isPublic(UUID userId);
}

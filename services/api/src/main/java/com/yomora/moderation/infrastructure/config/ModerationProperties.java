package com.yomora.moderation.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.UUID;

@ConfigurationProperties("yomora.moderation")
public record ModerationProperties(List<UUID> adminUserIds) {
    public ModerationProperties {
        adminUserIds = adminUserIds == null ? List.of() : List.copyOf(adminUserIds);
    }
}

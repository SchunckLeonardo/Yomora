package com.yomora.moderation.application;

import org.springframework.security.access.AccessDeniedException;

import java.util.Set;
import java.util.UUID;

public class AdminAccessPolicy {
    private final Set<UUID> administratorIds;

    public AdminAccessPolicy(Set<UUID> administratorIds) {
        this.administratorIds = Set.copyOf(administratorIds);
    }

    public void requireAdmin(UUID userId) {
        if (!administratorIds.contains(userId)) {
            throw new AccessDeniedException("Acesso restrito à moderação administrativa");
        }
    }
}

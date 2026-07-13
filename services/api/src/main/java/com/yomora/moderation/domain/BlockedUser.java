package com.yomora.moderation.domain;

import java.time.Instant;
import java.util.UUID;

public record BlockedUser(UUID id, UUID blockerId, UUID blockedId, Instant createdAt) {
}

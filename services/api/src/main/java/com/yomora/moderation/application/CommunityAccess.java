package com.yomora.moderation.application;

import java.time.Instant;

public record CommunityAccess(boolean allowed, Instant suspendedUntil, String reason) {
    public static CommunityAccess allowedAccess() {
        return new CommunityAccess(true, null, null);
    }
}

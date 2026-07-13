package com.yomora.library.domain;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record Shelf(
        UUID id,
        UUID userId,
        String name,
        boolean publicShelf,
        Set<UUID> userBookIds,
        Instant createdAt,
        Instant updatedAt
) {
}

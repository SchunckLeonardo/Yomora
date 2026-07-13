package com.yomora.library.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record UserBook(
        UUID id,
        UUID userId,
        UUID editionId,
        ReadingStatus status,
        int currentPage,
        Instant startedAt,
        Instant finishedAt,
        Integer rating,
        LocalDate targetFinishDate,
        Instant createdAt,
        Instant updatedAt
) {
}

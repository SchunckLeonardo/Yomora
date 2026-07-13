package com.yomora.catalog.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BookSearchResult(
        UUID workId,
        UUID editionId,
        String title,
        String description,
        List<String> authors,
        List<String> categories,
        String isbn10,
        String isbn13,
        String publisher,
        LocalDate publicationDate,
        String language,
        Integer pageCount,
        String coverUrl,
        String externalProvider,
        String externalId
) {
    public static BookSearchResult from(BookCandidate candidate) {
        return new BookSearchResult(
                UUID.randomUUID(),
                UUID.randomUUID(),
                candidate.title(),
                candidate.description(),
                List.copyOf(candidate.authors()),
                List.copyOf(candidate.categories()),
                normalizeIsbn(candidate.isbn10()),
                normalizeIsbn(candidate.isbn13()),
                candidate.publisher(),
                candidate.publicationDate(),
                candidate.language(),
                candidate.pageCount(),
                candidate.coverUrl(),
                candidate.externalProvider(),
                candidate.externalId()
        );
    }

    public static String normalizeIsbn(String isbn) {
        return isbn == null ? null : isbn.replaceAll("[^0-9Xx]", "").toUpperCase();
    }
}

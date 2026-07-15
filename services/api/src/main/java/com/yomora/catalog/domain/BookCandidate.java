package com.yomora.catalog.domain;

import java.time.LocalDate;
import java.util.List;

public record BookCandidate(
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
    public BookCandidate {
        language = BookLanguage.normalize(language);
    }
}

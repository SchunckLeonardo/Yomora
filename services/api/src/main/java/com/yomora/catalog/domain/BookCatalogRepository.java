package com.yomora.catalog.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookCatalogRepository {
    List<BookSearchResult> search(String query, String language, int limit);

    BookSearchResult save(BookCandidate candidate);

    default Optional<BookSearchResult> findEdition(UUID editionId) {
        return Optional.empty();
    }
}

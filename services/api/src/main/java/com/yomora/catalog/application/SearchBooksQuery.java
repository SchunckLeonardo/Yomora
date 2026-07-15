package com.yomora.catalog.application;

import com.yomora.catalog.domain.BookLanguage;

public record SearchBooksQuery(String query, String language, int limit) {
    public SearchBooksQuery {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("A busca não pode ser vazia");
        }
        language = BookLanguage.normalize(
                language == null || language.isBlank() ? BookLanguage.PORTUGUESE : language
        );
        limit = Math.max(1, Math.min(limit, 50));
    }
}

package com.yomora.catalog.application;

public record SearchBooksQuery(String query, String language, int limit) {
    public SearchBooksQuery {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("A busca não pode ser vazia");
        }
        language = language == null || language.isBlank() ? "pt" : language.trim().toLowerCase();
        limit = Math.max(1, Math.min(limit, 50));
    }
}

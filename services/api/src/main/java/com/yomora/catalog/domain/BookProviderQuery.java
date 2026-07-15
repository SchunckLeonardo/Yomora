package com.yomora.catalog.domain;

public record BookProviderQuery(String query, String language, int limit) {
    public BookProviderQuery {
        language = BookLanguage.normalize(language);
    }
}

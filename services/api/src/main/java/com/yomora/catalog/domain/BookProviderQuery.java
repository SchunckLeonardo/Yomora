package com.yomora.catalog.domain;

public record BookProviderQuery(String query, String language, int limit) {
}

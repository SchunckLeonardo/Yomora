package com.yomora.catalog.application;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchBooksQueryTest {

    @Test
    void acceptsPortugueseIso6391AndIso6393Codes() {
        assertThat(new SearchBooksQuery("Paul Washer", "pt", 20).language()).isEqualTo("por");
        assertThat(new SearchBooksQuery("Paul Washer", "por", 20).language()).isEqualTo("por");
    }
}

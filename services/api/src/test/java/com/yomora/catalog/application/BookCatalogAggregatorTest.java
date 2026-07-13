package com.yomora.catalog.application;

import com.yomora.catalog.domain.BookCandidate;
import com.yomora.catalog.domain.BookCatalogRepository;
import com.yomora.catalog.domain.BookProvider;
import com.yomora.catalog.domain.BookSearchResult;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookCatalogAggregatorTest {

    @Test
    void fallsBackAndDeduplicatesEditionsByIsbnBeforePersisting() {
        BookProvider unavailableGoogle = query -> {
            throw new BookProviderUnavailableException("google-books");
        };
        BookProvider openLibrary = query -> List.of(
                candidate("9780061120084", "open-library", "OL1M"),
                candidate("978-0-06-112008-4", "open-library", "OL1M-duplicate")
        );
        InMemoryCatalogRepository repository = new InMemoryCatalogRepository();
        BookCatalogAggregator aggregator = new BookCatalogAggregator(repository, unavailableGoogle, openLibrary);

        List<BookSearchResult> results = aggregator.search(new SearchBooksQuery("O Sol é para Todos", "pt", 20));

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().isbn13()).isEqualTo("9780061120084");
        assertThat(repository.saved).hasSize(1);
        assertThat(repository.saved.getFirst().language()).isEqualTo("pt");
    }

    @Test
    void doesNotCallFallbackWhenPrimaryProviderReturnsResults() {
        BookProvider google = query -> List.of(candidate("9780061120084", "google-books", "g-1"));
        BookProvider fallbackMustNotRun = query -> {
            throw new AssertionError("fallback não deveria ser chamado");
        };
        BookCatalogAggregator aggregator = new BookCatalogAggregator(
                new InMemoryCatalogRepository(), google, fallbackMustNotRun
        );

        assertThat(aggregator.search(new SearchBooksQuery("Harper Lee", "en", 10)))
                .singleElement()
                .extracting(BookSearchResult::externalProvider)
                .isEqualTo("google-books");
    }

    private static BookCandidate candidate(String isbn13, String provider, String externalId) {
        return new BookCandidate(
                "O Sol é para Todos",
                "Um clássico sobre justiça.",
                List.of("Harper Lee"),
                List.of("Ficção"),
                null,
                isbn13,
                "HarperCollins",
                LocalDate.of(2006, 5, 23),
                "pt",
                336,
                "https://example.test/cover.jpg",
                provider,
                externalId
        );
    }

    private static final class InMemoryCatalogRepository implements BookCatalogRepository {
        private final List<BookCandidate> saved = new ArrayList<>();

        @Override
        public List<BookSearchResult> search(String query, String language, int limit) {
            return List.of();
        }

        @Override
        public BookSearchResult save(BookCandidate candidate) {
            saved.add(candidate);
            return BookSearchResult.from(candidate);
        }
    }
}

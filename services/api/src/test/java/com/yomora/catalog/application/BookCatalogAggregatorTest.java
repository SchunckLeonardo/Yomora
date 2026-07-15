package com.yomora.catalog.application;

import com.yomora.catalog.domain.BookCandidate;
import com.yomora.catalog.domain.BookCatalogRepository;
import com.yomora.catalog.domain.BookProvider;
import com.yomora.catalog.domain.BookSearchResult;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        assertThat(repository.saved.getFirst().language()).isEqualTo("por");
    }

    @Test
    void queriesBothProvidersAndRanksExactAuthorMatchesFirst() {
        AtomicInteger googleCalls = new AtomicInteger();
        AtomicInteger openLibraryCalls = new AtomicInteger();
        BookProvider google = query -> {
            googleCalls.incrementAndGet();
            return List.of(
                    candidate("9780000000001", "google-books", "g-1", "Pregação reformada", List.of("Outro Autor"), "eng"),
                    candidate("9780000000002", "google-books", "g-2", "Teologia bíblica", List.of("Outro Autor"), "eng")
            );
        };
        BookProvider openLibrary = query -> {
            openLibraryCalls.incrementAndGet();
            return List.of(candidate(
                    "9780000000003",
                    "open-library",
                    "ol-1",
                    "O poder e a mensagem do evangelho",
                    List.of("Paul Washer"),
                    "por"
            ));
        };
        BookCatalogAggregator aggregator = new BookCatalogAggregator(
                new InMemoryCatalogRepository(), google, openLibrary
        );

        List<BookSearchResult> results = aggregator.search(new SearchBooksQuery("Paul Washer", "pt", 2));

        assertThat(googleCalls).hasValue(1);
        assertThat(openLibraryCalls).hasValue(1);
        assertThat(results).hasSize(2);
        assertThat(results.getFirst().authors()).containsExactly("Paul Washer");
        assertThat(results.getFirst().externalProvider()).isEqualTo("open-library");
    }

    @Test
    void deduplicatesTheSameEditionReturnedByBothProviders() {
        BookProvider google = query -> List.of(candidate("9780061120084", "google-books", "g-1"));
        BookProvider openLibrary = query -> List.of(candidate("978-0-06-112008-4", "open-library", "ol-1"));
        InMemoryCatalogRepository repository = new InMemoryCatalogRepository();
        BookCatalogAggregator aggregator = new BookCatalogAggregator(repository, google, openLibrary);

        List<BookSearchResult> results = aggregator.search(new SearchBooksQuery("Harper Lee", "por", 20));

        assertThat(results).hasSize(1);
        assertThat(repository.saved).hasSize(1);
    }

    @Test
    void doesNotExceedTheLimitWhenLocalResultsAlreadyFillThePage() {
        BookSearchResult localResult = BookSearchResult.from(candidate(
                "9780000000004",
                "local",
                "local-1",
                "Paul Washer local",
                List.of("Paul Washer"),
                "por"
        ));
        BookCatalogRepository repository = new BookCatalogRepository() {
            @Override
            public List<BookSearchResult> search(String query, String language, int limit) {
                return List.of(localResult);
            }

            @Override
            public BookSearchResult save(BookCandidate candidate) {
                throw new AssertionError("não deveria persistir além do limite");
            }
        };
        BookProvider google = query -> List.of(candidate("9780000000005", "google-books", "g-5"));
        BookProvider openLibrary = query -> List.of();
        BookCatalogAggregator aggregator = new BookCatalogAggregator(repository, google, openLibrary);

        assertThat(aggregator.search(new SearchBooksQuery("Paul Washer", "por", 1)))
                .containsExactly(localResult);
    }

    private static BookCandidate candidate(String isbn13, String provider, String externalId) {
        return candidate(
                isbn13,
                provider,
                externalId,
                "O Sol é para Todos",
                List.of("Harper Lee"),
                "pt"
        );
    }

    private static BookCandidate candidate(
            String isbn13,
            String provider,
            String externalId,
            String title,
            List<String> authors,
            String language
    ) {
        return new BookCandidate(
                title,
                "Um clássico sobre justiça.",
                authors,
                List.of("Ficção"),
                null,
                isbn13,
                "HarperCollins",
                LocalDate.of(2006, 5, 23),
                language,
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

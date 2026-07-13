package com.yomora.catalog.application;

import com.yomora.catalog.domain.BookCandidate;
import com.yomora.catalog.domain.BookCatalogRepository;
import com.yomora.catalog.domain.BookProvider;
import com.yomora.catalog.domain.BookProviderQuery;
import com.yomora.catalog.domain.BookSearchResult;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.cache.annotation.Cacheable;

public class BookCatalogAggregator {
    private final BookCatalogRepository repository;
    private final BookProvider primaryProvider;
    private final BookProvider fallbackProvider;

    public BookCatalogAggregator(
            BookCatalogRepository repository,
            BookProvider primaryProvider,
            BookProvider fallbackProvider
    ) {
        this.repository = repository;
        this.primaryProvider = primaryProvider;
        this.fallbackProvider = fallbackProvider;
    }

    @Cacheable(cacheNames = "book-search", key = "#query.query() + ':' + #query.language() + ':' + #query.limit()")
    public List<BookSearchResult> search(SearchBooksQuery query) {
        Map<String, BookSearchResult> results = new LinkedHashMap<>();
        repository.search(query.query(), query.language(), query.limit())
                .forEach(result -> results.putIfAbsent(key(result), result));

        BookProviderQuery providerQuery = new BookProviderQuery(query.query(), query.language(), query.limit());
        List<BookCandidate> candidates = safeSearch(primaryProvider, providerQuery);
        if (candidates.isEmpty()) {
            candidates = safeSearch(fallbackProvider, providerQuery);
        }

        for (BookCandidate candidate : candidates) {
            BookCandidate normalized = normalize(candidate, query.language());
            String key = key(normalized);
            if (!results.containsKey(key)) {
                results.put(key, repository.save(normalized));
            }
            if (results.size() >= query.limit()) {
                break;
            }
        }
        return new ArrayList<>(results.values());
    }

    private List<BookCandidate> safeSearch(BookProvider provider, BookProviderQuery query) {
        try {
            return provider.search(query);
        } catch (BookProviderUnavailableException exception) {
            return List.of();
        }
    }

    private BookCandidate normalize(BookCandidate candidate, String requestedLanguage) {
        return new BookCandidate(
                candidate.title().trim(),
                candidate.description() == null ? "" : candidate.description().trim(),
                candidate.authors().stream().map(String::trim).filter(value -> !value.isBlank()).distinct().toList(),
                candidate.categories().stream().map(String::trim).filter(value -> !value.isBlank()).distinct().toList(),
                BookSearchResult.normalizeIsbn(candidate.isbn10()),
                BookSearchResult.normalizeIsbn(candidate.isbn13()),
                candidate.publisher(),
                candidate.publicationDate(),
                candidate.language() == null || candidate.language().isBlank() ? requestedLanguage : candidate.language().toLowerCase(),
                candidate.pageCount(),
                candidate.coverUrl(),
                candidate.externalProvider(),
                candidate.externalId()
        );
    }

    private String key(BookCandidate candidate) {
        if (candidate.isbn13() != null && !candidate.isbn13().isBlank()) {
            return "isbn13:" + BookSearchResult.normalizeIsbn(candidate.isbn13());
        }
        return textKey(candidate.title(), candidate.authors().stream().findFirst().orElse(""));
    }

    private String key(BookSearchResult result) {
        if (result.isbn13() != null && !result.isbn13().isBlank()) {
            return "isbn13:" + BookSearchResult.normalizeIsbn(result.isbn13());
        }
        return textKey(result.title(), result.authors().stream().findFirst().orElse(""));
    }

    private String textKey(String title, String author) {
        return Normalizer.normalize(title + ":" + author, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "");
    }
}

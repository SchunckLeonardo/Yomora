package com.yomora.catalog.application;

import com.yomora.catalog.domain.BookCandidate;
import com.yomora.catalog.domain.BookCatalogRepository;
import com.yomora.catalog.domain.BookLanguage;
import com.yomora.catalog.domain.BookProvider;
import com.yomora.catalog.domain.BookProviderQuery;
import com.yomora.catalog.domain.BookSearchResult;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.cache.annotation.Cacheable;

public class BookCatalogAggregator {
    private final BookCatalogRepository repository;
    private final List<BookProvider> providers;

    public BookCatalogAggregator(
            BookCatalogRepository repository,
            BookProvider firstProvider,
            BookProvider secondProvider
    ) {
        this.repository = repository;
        this.providers = List.of(firstProvider, secondProvider);
    }

    @Cacheable(cacheNames = "book-search", key = "#query.query() + ':' + #query.language() + ':' + #query.limit()")
    public List<BookSearchResult> search(SearchBooksQuery query) {
        Map<String, BookSearchResult> results = new LinkedHashMap<>();
        repository.search(query.query(), query.language(), query.limit())
                .forEach(result -> results.putIfAbsent(key(result), result));

        BookProviderQuery providerQuery = new BookProviderQuery(query.query(), query.language(), query.limit());
        List<BookCandidate> candidates = new ArrayList<>();
        providers.forEach(provider -> candidates.addAll(safeSearch(provider, providerQuery)));

        List<BookCandidate> rankedCandidates = candidates.stream()
                .map(candidate -> normalize(candidate, query.language()))
                .sorted(Comparator.comparingInt(
                        (BookCandidate candidate) -> relevance(candidate, query)
                ).reversed())
                .toList();

        for (BookCandidate normalized : rankedCandidates) {
            if (results.size() >= query.limit()) {
                break;
            }
            String key = key(normalized);
            if (!results.containsKey(key)) {
                results.put(key, repository.save(normalized));
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
                BookLanguage.normalize(
                        candidate.language() == null || candidate.language().isBlank()
                                ? requestedLanguage
                                : candidate.language()
                ),
                candidate.pageCount(),
                candidate.coverUrl(),
                candidate.externalProvider(),
                candidate.externalId()
        );
    }

    private int relevance(BookCandidate candidate, SearchBooksQuery query) {
        String searched = normalizedText(query.query());
        int score = 0;
        for (String author : candidate.authors()) {
            String normalizedAuthor = normalizedText(author);
            if (normalizedAuthor.equals(searched)) {
                score = Math.max(score, 100);
            } else if (normalizedAuthor.contains(searched) || searched.contains(normalizedAuthor)) {
                score = Math.max(score, 80);
            }
        }

        String title = normalizedText(candidate.title());
        if (title.equals(searched)) {
            score += 60;
        } else if (title.contains(searched)) {
            score += 40;
        }
        if (query.language().equals(BookLanguage.normalize(candidate.language()))) {
            score += 10;
        }
        return score;
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
        return normalizedText(title + ":" + author);
    }

    private String normalizedText(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "");
    }
}

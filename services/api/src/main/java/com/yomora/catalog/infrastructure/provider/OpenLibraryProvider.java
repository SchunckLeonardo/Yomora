package com.yomora.catalog.infrastructure.provider;

import com.yomora.catalog.application.BookProviderUnavailableException;
import com.yomora.catalog.domain.BookCandidate;
import com.yomora.catalog.domain.BookProvider;
import com.yomora.catalog.domain.BookProviderQuery;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import java.net.http.HttpClient;
import java.time.Duration;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component("openLibraryProvider")
class OpenLibraryProvider implements BookProvider {
    private final RestClient client;

    OpenLibraryProvider(RestClient.Builder builder) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build()
        );
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        this.client = builder.clone().requestFactory(requestFactory)
                .baseUrl("https://openlibrary.org").build();
    }

    @Override
    public List<BookCandidate> search(BookProviderQuery query) {
        OpenLibraryResponse response = executeWithSingleRetry(query);
        if (response == null || response.docs() == null) {
            return List.of();
        }
        return response.docs().stream()
                .map(document -> toCandidate(document, query.language()))
                .filter(Objects::nonNull)
                .limit(query.limit())
                .toList();
    }

    private OpenLibraryResponse executeWithSingleRetry(BookProviderQuery query) {
        RestClientException lastFailure = null;
        for (int attempt = 0; attempt < 2; attempt++) {
            try {
                return client.get()
                        .uri(uri -> uri.path("/search.json")
                                .queryParam("q", query.query())
                                .queryParam("language", query.language())
                                .queryParam("limit", query.limit())
                                .queryParam("fields", "key,title,author_name,isbn,publisher,first_publish_year,language,cover_i,number_of_pages_median,subject")
                                .build())
                        .retrieve()
                        .body(OpenLibraryResponse.class);
            } catch (RestClientException exception) {
                lastFailure = exception;
            }
        }
        throw new BookProviderUnavailableException("open-library", lastFailure);
    }

    private BookCandidate toCandidate(OpenLibraryDocument document, String requestedLanguage) {
        if (document.title() == null || document.title().isBlank()) {
            return null;
        }
        String isbn10 = firstIsbn(document.isbn(), 10);
        String isbn13 = firstIsbn(document.isbn(), 13);
        String language = document.language() == null || document.language().isEmpty()
                ? requestedLanguage
                : document.language().getFirst();
        return new BookCandidate(
                document.title(),
                "",
                document.author_name() == null ? List.of("Autor desconhecido") : document.author_name(),
                document.subject() == null ? List.of() : document.subject().stream().limit(8).toList(),
                isbn10,
                isbn13,
                document.publisher() == null || document.publisher().isEmpty() ? null : document.publisher().getFirst(),
                document.first_publish_year() == null ? null : LocalDate.of(document.first_publish_year(), 1, 1),
                language,
                document.number_of_pages_median(),
                document.cover_i() == null ? null : "https://covers.openlibrary.org/b/id/" + document.cover_i() + "-L.jpg",
                "open-library",
                document.key()
        );
    }

    private String firstIsbn(List<String> isbns, int length) {
        if (isbns == null) {
            return null;
        }
        return isbns.stream()
                .map(value -> value.replaceAll("[^0-9Xx]", ""))
                .filter(value -> value.length() == length)
                .findFirst()
                .orElse(null);
    }

    private record OpenLibraryResponse(List<OpenLibraryDocument> docs) {
    }

    private record OpenLibraryDocument(
            String key,
            String title,
            List<String> author_name,
            List<String> isbn,
            List<String> publisher,
            Integer first_publish_year,
            List<String> language,
            Long cover_i,
            Integer number_of_pages_median,
            List<String> subject
    ) {
    }
}

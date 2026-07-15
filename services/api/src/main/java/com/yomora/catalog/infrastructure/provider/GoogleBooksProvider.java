package com.yomora.catalog.infrastructure.provider;

import com.yomora.catalog.application.BookProviderUnavailableException;
import com.yomora.catalog.domain.BookCandidate;
import com.yomora.catalog.domain.BookLanguage;
import com.yomora.catalog.domain.BookProvider;
import com.yomora.catalog.domain.BookProviderQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import java.net.http.HttpClient;
import java.time.Duration;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component("googleBooksProvider")
class GoogleBooksProvider implements BookProvider {
    private final RestClient client;
    private final String apiKey;

    GoogleBooksProvider(RestClient.Builder builder, @Value("${GOOGLE_BOOKS_API_KEY:}") String apiKey) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build()
        );
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        this.client = builder.clone().requestFactory(requestFactory)
                .baseUrl("https://www.googleapis.com").build();
        this.apiKey = apiKey;
    }

    @Override
    public List<BookCandidate> search(BookProviderQuery query) {
        GoogleBooksResponse response = executeWithSingleRetry(query);
        if (response == null || response.items() == null) {
            return List.of();
        }
        return response.items().stream()
                .map(this::toCandidate)
                .filter(Objects::nonNull)
                .toList();
    }

    private GoogleBooksResponse executeWithSingleRetry(BookProviderQuery query) {
        RestClientException lastFailure = null;
        for (int attempt = 0; attempt < 2; attempt++) {
            try {
                return client.get()
                        .uri(uri -> {
                            Map<String, Object> variables = new LinkedHashMap<>();
                            variables.put("query", query.query());
                            variables.put("language", BookLanguage.toIso6391(query.language()));
                            variables.put("limit", query.limit());
                            var builder = uri.path("/books/v1/volumes")
                                    .queryParam("q", "{query}")
                                    .queryParam("langRestrict", "{language}")
                                    .queryParam("maxResults", "{limit}")
                                    .queryParam("printType", "books");
                            if (!apiKey.isBlank()) {
                                builder.queryParam("key", "{apiKey}");
                                variables.put("apiKey", apiKey);
                            }
                            return builder.build(variables);
                        })
                        .retrieve()
                        .body(GoogleBooksResponse.class);
            } catch (RestClientException exception) {
                lastFailure = exception;
            }
        }
        throw new BookProviderUnavailableException("google-books", lastFailure);
    }

    private BookCandidate toCandidate(GoogleVolume volume) {
        GoogleVolumeInfo info = volume.volumeInfo();
        if (info == null || info.title() == null || info.title().isBlank()) {
            return null;
        }
        String isbn10 = identifier(info.industryIdentifiers(), "ISBN_10");
        String isbn13 = identifier(info.industryIdentifiers(), "ISBN_13");
        return new BookCandidate(
                info.title(),
                info.description() == null ? "" : info.description(),
                info.authors() == null ? List.of("Autor desconhecido") : info.authors(),
                info.categories() == null ? List.of() : info.categories(),
                isbn10,
                isbn13,
                info.publisher(),
                parseDate(info.publishedDate()),
                info.language(),
                info.pageCount(),
                info.imageLinks() == null ? null : secure(info.imageLinks().thumbnail()),
                "google-books",
                volume.id()
        );
    }

    private String identifier(List<GoogleIndustryIdentifier> identifiers, String type) {
        if (identifiers == null) {
            return null;
        }
        return identifiers.stream()
                .filter(identifier -> type.equals(identifier.type()))
                .map(GoogleIndustryIdentifier::identifier)
                .findFirst()
                .orElse(null);
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            if (value.length() == 4) {
                return Year.parse(value).atDay(1);
            }
            if (value.length() == 7) {
                return YearMonth.parse(value).atDay(1);
            }
            return LocalDate.parse(value.substring(0, Math.min(value.length(), 10)));
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private String secure(String url) {
        return url == null ? null : url.replace("http://", "https://");
    }

    private record GoogleBooksResponse(List<GoogleVolume> items) {
    }

    private record GoogleVolume(String id, GoogleVolumeInfo volumeInfo) {
    }

    private record GoogleVolumeInfo(
            String title,
            String description,
            List<String> authors,
            List<String> categories,
            List<GoogleIndustryIdentifier> industryIdentifiers,
            String publisher,
            String publishedDate,
            String language,
            Integer pageCount,
            GoogleImageLinks imageLinks
    ) {
    }

    private record GoogleIndustryIdentifier(String type, String identifier) {
    }

    private record GoogleImageLinks(String thumbnail) {
    }
}

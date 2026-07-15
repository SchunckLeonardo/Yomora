package com.yomora.catalog.infrastructure.provider;

import com.yomora.catalog.domain.BookProviderQuery;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalBookProviderUriTest {

    @Test
    void googleBooksEncodesTemplateLikeSearchTermsAsData() {
        AtomicReference<URI> requestedUri = new AtomicReference<>();
        GoogleBooksProvider provider = new GoogleBooksProvider(capturingBuilder(requestedUri), "api-key");

        assertThat(provider.search(new BookProviderQuery("{editionId}", "por", 20))).isEmpty();
        assertThat(requestedUri.get().getRawQuery()).contains("q=%7BeditionId%7D");
        assertThat(requestedUri.get().getRawQuery()).contains("langRestrict=pt");
    }

    @Test
    void openLibraryEncodesTemplateLikeSearchTermsAsData() {
        AtomicReference<URI> requestedUri = new AtomicReference<>();
        OpenLibraryProvider provider = new OpenLibraryProvider(capturingBuilder(requestedUri));

        assertThat(provider.search(new BookProviderQuery("{editionId}", "por", 20))).isEmpty();
        assertThat(requestedUri.get().getRawQuery()).contains("q=%7BeditionId%7D");
        assertThat(requestedUri.get().getRawQuery()).contains("lang=pt");
        assertThat(requestedUri.get().getRawQuery()).doesNotContain("language=");
    }

    @Test
    void googleBooksNormalizesPortugueseResponseMetadataToIso6393() {
        GoogleBooksProvider provider = new GoogleBooksProvider(responseBuilder("""
                {"items":[{"id":"g-1","volumeInfo":{"title":"Evangelho","authors":["Paul Washer"],"language":"pt"}}]}
                """), "api-key");

        assertThat(provider.search(new BookProviderQuery("Paul Washer", "por", 20)))
                .singleElement()
                .extracting(candidate -> candidate.language())
                .isEqualTo("por");
    }

    @Test
    void openLibraryKeepsPortugueseResponseMetadataInIso6393() {
        OpenLibraryProvider provider = new OpenLibraryProvider(responseBuilder("""
                {"docs":[{"key":"/works/OL1W","title":"Evangelho","author_name":["Paul Washer"],"language":["por"]}]}
                """));

        assertThat(provider.search(new BookProviderQuery("Paul Washer", "por", 20)))
                .singleElement()
                .extracting(candidate -> candidate.language())
                .isEqualTo("por");
    }

    private RestClient.Builder capturingBuilder(AtomicReference<URI> requestedUri) {
        return responseBuilder("{}", requestedUri);
    }

    private RestClient.Builder responseBuilder(String response) {
        return responseBuilder(response, new AtomicReference<>());
    }

    private RestClient.Builder responseBuilder(String response, AtomicReference<URI> requestedUri) {
        return RestClient.builder().requestInterceptor((request, body, execution) -> {
            requestedUri.set(request.getURI());
            MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                    response.getBytes(StandardCharsets.UTF_8),
                    HttpStatus.OK
            );
            mockResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return mockResponse;
        });
    }
}

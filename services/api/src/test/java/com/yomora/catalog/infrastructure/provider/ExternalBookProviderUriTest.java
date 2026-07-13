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

        assertThat(provider.search(new BookProviderQuery("{editionId}", "pt", 20))).isEmpty();
        assertThat(requestedUri.get().getRawQuery()).contains("q=%7BeditionId%7D");
    }

    @Test
    void openLibraryEncodesTemplateLikeSearchTermsAsData() {
        AtomicReference<URI> requestedUri = new AtomicReference<>();
        OpenLibraryProvider provider = new OpenLibraryProvider(capturingBuilder(requestedUri));

        assertThat(provider.search(new BookProviderQuery("{editionId}", "pt", 20))).isEmpty();
        assertThat(requestedUri.get().getRawQuery()).contains("q=%7BeditionId%7D");
    }

    private RestClient.Builder capturingBuilder(AtomicReference<URI> requestedUri) {
        return RestClient.builder().requestInterceptor((request, body, execution) -> {
            requestedUri.set(request.getURI());
            MockClientHttpResponse response = new MockClientHttpResponse(
                    "{}".getBytes(StandardCharsets.UTF_8),
                    HttpStatus.OK
            );
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response;
        });
    }
}

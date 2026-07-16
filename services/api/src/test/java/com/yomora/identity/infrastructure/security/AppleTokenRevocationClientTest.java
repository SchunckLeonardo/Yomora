package com.yomora.identity.infrastructure.security;

import com.yomora.identity.domain.ExternalProvider;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;

class AppleTokenRevocationClientTest {
    @Test
    void revokesTheRefreshTokenAtApple() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://appleid.apple.com");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://appleid.apple.com/auth/revoke"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("token=apple-refresh")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("client_id=app.yomora.ios")))
                .andRespond(withNoContent());
        AppleTokenRevocationClient client = new AppleTokenRevocationClient(
                builder.build(), "app.yomora.ios", () -> "client-secret"
        );

        client.revoke(ExternalProvider.APPLE, "apple-refresh");

        server.verify();
    }
}

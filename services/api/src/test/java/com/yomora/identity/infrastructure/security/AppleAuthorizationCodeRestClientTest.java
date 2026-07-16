package com.yomora.identity.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AppleAuthorizationCodeRestClientTest {
    @Test
    void exchangesTheCodeWithoutExposingThePrivateCredentialToTheApp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://appleid.apple.com");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://appleid.apple.com/auth/token"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("code=single-use-code")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("client_id=app.yomora.ios")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("client_secret=signed-client-secret")))
                .andRespond(withSuccess("""
                        {"access_token":"access","refresh_token":"apple-refresh","token_type":"Bearer","expires_in":3600}
                        """, MediaType.APPLICATION_JSON));
        AppleAuthorizationCodeRestClient client = new AppleAuthorizationCodeRestClient(
                builder.build(), "app.yomora.ios", () -> "signed-client-secret"
        );

        assertThat(client.exchange("single-use-code")).isEqualTo("apple-refresh");
        server.verify();
    }
}

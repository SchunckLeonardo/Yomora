package com.yomora.identity.infrastructure.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yomora.identity.application.InvalidExternalAuthorizationCodeException;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.function.Supplier;

final class AppleAuthorizationCodeRestClient implements AppleAuthorizationCodeClient {
    private final RestClient restClient;
    private final String clientId;
    private final Supplier<String> clientSecret;

    AppleAuthorizationCodeRestClient(
            RestClient restClient,
            String clientId,
            Supplier<String> clientSecret
    ) {
        this.restClient = restClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public String exchange(String authorizationCode) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", authorizationCode);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret.get());
        try {
            AppleTokenResponse response = restClient.post()
                    .uri("/auth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(AppleTokenResponse.class);
            if (response == null || response.refreshToken() == null || response.refreshToken().isBlank()) {
                throw new InvalidExternalAuthorizationCodeException();
            }
            return response.refreshToken();
        } catch (RestClientException exception) {
            throw new InvalidExternalAuthorizationCodeException(exception);
        }
    }

    private record AppleTokenResponse(@JsonProperty("refresh_token") String refreshToken) {
    }
}

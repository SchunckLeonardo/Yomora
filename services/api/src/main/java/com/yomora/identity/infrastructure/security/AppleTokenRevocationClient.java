package com.yomora.identity.infrastructure.security;

import com.yomora.identity.application.ExternalTokenRevocationException;
import com.yomora.identity.application.ExternalTokenRevoker;
import com.yomora.identity.domain.ExternalProvider;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.function.Supplier;

final class AppleTokenRevocationClient implements ExternalTokenRevoker {
    private final RestClient restClient;
    private final String clientId;
    private final Supplier<String> clientSecret;

    AppleTokenRevocationClient(RestClient restClient, String clientId, Supplier<String> clientSecret) {
        this.restClient = restClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public void revoke(ExternalProvider provider, String refreshToken) {
        if (provider != ExternalProvider.APPLE) {
            throw new IllegalArgumentException("Provedor de identidade não suportado");
        }
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret.get());
        form.add("token", refreshToken);
        form.add("token_type_hint", "refresh_token");
        try {
            restClient.post()
                    .uri("/auth/revoke")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            throw new ExternalTokenRevocationException(exception);
        }
    }
}

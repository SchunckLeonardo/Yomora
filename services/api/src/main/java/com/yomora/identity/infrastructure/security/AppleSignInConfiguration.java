package com.yomora.identity.infrastructure.security;

import com.yomora.identity.application.ExternalIdentityAuthenticator;
import com.yomora.identity.application.ExternalTokenRevoker;
import com.yomora.identity.application.ExternalAccountEventVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.client.RestClient;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "yomora.apple.enabled", havingValue = "true")
class AppleSignInConfiguration {
    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    @Bean
    AppleClientSecretGenerator appleClientSecretGenerator(
            @Value("${yomora.apple.team-id:}") String teamId,
            @Value("${yomora.apple.client-id:}") String clientId,
            @Value("${yomora.apple.key-id:}") String keyId,
            @Value("${yomora.apple.private-key:}") String privateKey,
            Clock clock
    ) {
        return new AppleClientSecretGenerator(teamId, clientId, keyId, privateKey, clock);
    }

    @Bean
    AppleIdentityTokenVerifier appleIdentityTokenVerifier(
            @Value("${yomora.apple.client-id:}") String clientId,
            @Value("${yomora.apple.jwk-set-uri:https://appleid.apple.com/auth/keys}") String jwkSetUri
    ) {
        NimbusJwtDecoder decoder = appleDecoder(clientId, jwkSetUri);
        return new AppleJwtIdentityTokenVerifier(decoder);
    }

    @Bean
    ExternalAccountEventVerifier appleAccountEventVerifier(
            @Value("${yomora.apple.client-id:}") String clientId,
            @Value("${yomora.apple.jwk-set-uri:https://appleid.apple.com/auth/keys}") String jwkSetUri,
            ObjectMapper objectMapper
    ) {
        return new AppleAccountEventJwtVerifier(appleDecoder(clientId, jwkSetUri), objectMapper);
    }

    private NimbusJwtDecoder appleDecoder(String clientId, String jwkSetUri) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        OAuth2TokenValidator<Jwt> audience = jwt -> jwt.getAudience().contains(clientId)
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error(
                        "invalid_token", "Audience Apple inválida", null
                ));
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(APPLE_ISSUER), audience
        ));
        return decoder;
    }

    @Bean
    AppleAuthorizationCodeClient appleAuthorizationCodeClient(
            RestClient.Builder builder,
            @Value("${yomora.apple.client-id:}") String clientId,
            AppleClientSecretGenerator clientSecretGenerator
    ) {
        return new AppleAuthorizationCodeRestClient(
                builder.baseUrl(APPLE_ISSUER).build(), clientId, clientSecretGenerator::generate
        );
    }

    @Bean
    ExternalIdentityAuthenticator appleExternalIdentityAuthenticator(
            AppleIdentityTokenVerifier tokenVerifier,
            AppleAuthorizationCodeClient tokenClient
    ) {
        return new AppleExternalIdentityAuthenticator(tokenVerifier, tokenClient);
    }

    @Bean
    ExternalTokenRevoker appleExternalTokenRevoker(
            RestClient.Builder builder,
            @Value("${yomora.apple.client-id:}") String clientId,
            AppleClientSecretGenerator clientSecretGenerator
    ) {
        return new AppleTokenRevocationClient(
                builder.clone().baseUrl(APPLE_ISSUER).build(), clientId, clientSecretGenerator::generate
        );
    }
}

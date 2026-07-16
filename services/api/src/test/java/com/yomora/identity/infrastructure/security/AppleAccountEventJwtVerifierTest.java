package com.yomora.identity.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yomora.identity.application.ExternalAccountEvent;
import com.yomora.identity.application.ExternalAccountEventType;
import com.yomora.identity.domain.ExternalProvider;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AppleAccountEventJwtVerifierTest {
    @Test
    void readsConsentRevocationFromTheSignedAppleEventsClaim() {
        Instant now = Instant.parse("2026-07-16T12:00:00Z");
        JwtDecoder decoder = token -> Jwt.withTokenValue(token)
                .header("alg", "RS256")
                .subject("event-id")
                .claim("events", "{\"type\":\"consent-revoked\",\"sub\":\"apple-subject\"}")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(300))
                .build();
        AppleAccountEventJwtVerifier verifier = new AppleAccountEventJwtVerifier(decoder, new ObjectMapper());

        ExternalAccountEvent event = verifier.verify("signed-events-token");

        assertThat(event).isEqualTo(new ExternalAccountEvent(
                ExternalProvider.APPLE, "apple-subject", ExternalAccountEventType.CONSENT_REVOKED
        ));
        assertThat(event.type().revokesAccess()).isTrue();
    }

    @Test
    void recognizesTheAppleAccountDeletionEventName() {
        assertThat(ExternalAccountEventType.fromProviderValue("account-delete"))
                .isEqualTo(ExternalAccountEventType.ACCOUNT_DELETE);
    }
}

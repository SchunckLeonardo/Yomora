package com.yomora.identity.infrastructure.security;

import com.yomora.identity.application.InvalidExternalIdentityTokenException;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AppleJwtIdentityTokenVerifierTest {
    private static final String RAW_NONCE = "random-raw-nonce";

    @Test
    void acceptsClaimsOnlyWhenTheNonceMatchesTheOriginalRequest() {
        JwtDecoder decoder = token -> jwt(hash(RAW_NONCE));
        AppleJwtIdentityTokenVerifier verifier = new AppleJwtIdentityTokenVerifier(decoder);

        AppleIdentityClaims claims = verifier.verify("signed-token", RAW_NONCE);

        assertThat(claims).isEqualTo(new AppleIdentityClaims(
                "apple-subject", "reader@privaterelay.appleid.com", true
        ));
    }

    @Test
    void rejectsAReplayWithAnotherNonce() {
        JwtDecoder decoder = token -> jwt(hash("different-nonce"));
        AppleJwtIdentityTokenVerifier verifier = new AppleJwtIdentityTokenVerifier(decoder);

        assertThatThrownBy(() -> verifier.verify("signed-token", RAW_NONCE))
                .isInstanceOf(InvalidExternalIdentityTokenException.class);
    }

    private Jwt jwt(String nonce) {
        Instant now = Instant.parse("2026-07-16T12:00:00Z");
        return Jwt.withTokenValue("signed-token")
                .header("alg", "RS256")
                .subject("apple-subject")
                .claim("email", "reader@privaterelay.appleid.com")
                .claim("email_verified", "true")
                .claim("nonce", nonce)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(300))
                .build();
    }

    private String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }
}

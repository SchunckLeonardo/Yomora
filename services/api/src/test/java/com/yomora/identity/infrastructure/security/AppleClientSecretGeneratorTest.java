package com.yomora.identity.infrastructure.security;

import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPublicKey;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class AppleClientSecretGeneratorTest {
    @Test
    void signsAShortLivedClientSecretWithTheConfiguredAppleKey() throws Exception {
        KeyPairGenerator keys = KeyPairGenerator.getInstance("EC");
        keys.initialize(256);
        KeyPair keyPair = keys.generateKeyPair();
        String pem = "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(keyPair.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----";
        Instant now = Instant.parse("2026-07-16T12:00:00Z");
        AppleClientSecretGenerator generator = new AppleClientSecretGenerator(
                "TEAM123", "app.yomora.ios", "KEY123", pem, Clock.fixed(now, ZoneOffset.UTC)
        );

        SignedJWT jwt = SignedJWT.parse(generator.generate());

        assertThat(jwt.verify(new ECDSAVerifier((ECPublicKey) keyPair.getPublic()))).isTrue();
        assertThat(jwt.getHeader().getKeyID()).isEqualTo("KEY123");
        assertThat(jwt.getJWTClaimsSet().getIssuer()).isEqualTo("TEAM123");
        assertThat(jwt.getJWTClaimsSet().getSubject()).isEqualTo("app.yomora.ios");
        assertThat(jwt.getJWTClaimsSet().getAudience()).containsExactly("https://appleid.apple.com");
        assertThat(jwt.getJWTClaimsSet().getIssueTime()).isEqualTo(java.util.Date.from(now));
        assertThat(jwt.getJWTClaimsSet().getExpirationTime())
                .isEqualTo(java.util.Date.from(now.plusSeconds(300)));
    }
}

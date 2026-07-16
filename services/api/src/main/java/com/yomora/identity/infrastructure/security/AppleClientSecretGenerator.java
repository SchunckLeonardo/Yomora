package com.yomora.identity.infrastructure.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

final class AppleClientSecretGenerator {
    private final String teamId;
    private final String clientId;
    private final String keyId;
    private final ECPrivateKey privateKey;
    private final Clock clock;

    AppleClientSecretGenerator(
            String teamId,
            String clientId,
            String keyId,
            String privateKeyPem,
            Clock clock
    ) {
        this.teamId = require(teamId, "APPLE_TEAM_ID");
        this.clientId = require(clientId, "APPLE_CLIENT_ID");
        this.keyId = require(keyId, "APPLE_KEY_ID");
        this.privateKey = parsePrivateKey(require(privateKeyPem, "APPLE_PRIVATE_KEY"));
        this.clock = clock;
    }

    String generate() {
        Instant now = clock.instant();
        SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(keyId).build(),
                new JWTClaimsSet.Builder()
                        .issuer(teamId)
                        .subject(clientId)
                        .audience("https://appleid.apple.com")
                        .issueTime(Date.from(now))
                        .expirationTime(Date.from(now.plusSeconds(300)))
                        .build()
        );
        try {
            jwt.sign(new ECDSASigner(privateKey));
            return jwt.serialize();
        } catch (JOSEException exception) {
            throw new IllegalStateException("Não foi possível assinar o client secret da Apple", exception);
        }
    }

    private ECPrivateKey parsePrivateKey(String pem) {
        String normalized = pem.replace("\\n", "\n")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        try {
            byte[] encoded = Base64.getDecoder().decode(normalized);
            return (ECPrivateKey) KeyFactory.getInstance("EC")
                    .generatePrivate(new PKCS8EncodedKeySpec(encoded));
        } catch (GeneralSecurityException | IllegalArgumentException exception) {
            throw new IllegalStateException("APPLE_PRIVATE_KEY inválida", exception);
        }
    }

    private String require(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " é obrigatório quando o login Apple está habilitado");
        }
        return value;
    }
}

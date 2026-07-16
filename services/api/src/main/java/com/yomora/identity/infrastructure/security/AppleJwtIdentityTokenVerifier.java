package com.yomora.identity.infrastructure.security;

import com.yomora.identity.application.InvalidExternalIdentityTokenException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

final class AppleJwtIdentityTokenVerifier implements AppleIdentityTokenVerifier {
    private final JwtDecoder decoder;

    AppleJwtIdentityTokenVerifier(JwtDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public AppleIdentityClaims verify(String identityToken, String rawNonce) {
        try {
            Jwt jwt = decoder.decode(identityToken);
            String nonce = jwt.getClaimAsString("nonce");
            if (!securelyEquals(hash(rawNonce), nonce)) {
                throw new InvalidExternalIdentityTokenException();
            }
            String subject = jwt.getSubject();
            String email = jwt.getClaimAsString("email");
            if (subject == null || subject.isBlank() || email == null || email.isBlank()) {
                throw new InvalidExternalIdentityTokenException();
            }
            return new AppleIdentityClaims(subject, email, emailVerified(jwt));
        } catch (JwtException | IllegalArgumentException exception) {
            if (exception instanceof InvalidExternalIdentityTokenException invalid) {
                throw invalid;
            }
            throw new InvalidExternalIdentityTokenException(exception);
        }
    }

    private boolean emailVerified(Jwt jwt) {
        Object claim = jwt.getClaim("email_verified");
        return Boolean.TRUE.equals(claim) || "true".equalsIgnoreCase(String.valueOf(claim));
    }

    private String hash(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidExternalIdentityTokenException();
        }
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 indisponível", exception);
        }
    }

    private boolean securelyEquals(String expected, String actual) {
        return actual != null && MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.US_ASCII),
                actual.getBytes(StandardCharsets.US_ASCII)
        );
    }
}

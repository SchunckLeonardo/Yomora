package com.yomora.identity.infrastructure.security;

import com.yomora.identity.application.AccessTokenIssuer;
import com.yomora.identity.domain.User;
import com.yomora.shared.config.SecurityProperties;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
class JwtAccessTokenIssuer implements AccessTokenIssuer {
    private final JwtEncoder encoder;
    private final SecurityProperties properties;
    private final Clock clock;

    JwtAccessTokenIssuer(JwtEncoder encoder, SecurityProperties properties, Clock clock) {
        this.encoder = encoder;
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    public String issue(User user) {
        Instant issuedAt = clock.instant();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plus(properties.accessTokenMinutes(), ChronoUnit.MINUTES))
                .subject(user.id().toString())
                .claim("username", user.username())
                .claim("email", user.email())
                .claim("scope", "user")
                .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public long expiresInSeconds() {
        return properties.accessTokenMinutes() * 60;
    }
}

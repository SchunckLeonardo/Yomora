package com.yomora.identity.infrastructure.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yomora.identity.application.ExternalAccountEvent;
import com.yomora.identity.application.ExternalAccountEventType;
import com.yomora.identity.application.ExternalAccountEventVerifier;
import com.yomora.identity.application.InvalidExternalIdentityTokenException;
import com.yomora.identity.domain.ExternalProvider;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.util.Map;

final class AppleAccountEventJwtVerifier implements ExternalAccountEventVerifier {
    private final JwtDecoder decoder;
    private final ObjectMapper objectMapper;

    AppleAccountEventJwtVerifier(JwtDecoder decoder, ObjectMapper objectMapper) {
        this.decoder = decoder;
        this.objectMapper = objectMapper;
    }

    @Override
    public ExternalAccountEvent verify(String signedPayload) {
        try {
            Jwt jwt = decoder.decode(signedPayload);
            Map<String, Object> events = events(jwt.getClaim("events"));
            String subject = String.valueOf(events.get("sub"));
            String type = String.valueOf(events.get("type"));
            if (subject.isBlank() || "null".equals(subject) || type.isBlank() || "null".equals(type)) {
                throw new InvalidExternalIdentityTokenException();
            }
            return new ExternalAccountEvent(
                    ExternalProvider.APPLE, subject, ExternalAccountEventType.fromProviderValue(type)
            );
        } catch (JwtException | JsonProcessingException | IllegalArgumentException exception) {
            if (exception instanceof InvalidExternalIdentityTokenException invalid) {
                throw invalid;
            }
            throw new InvalidExternalIdentityTokenException(exception);
        }
    }

    private Map<String, Object> events(Object claim) throws JsonProcessingException {
        if (claim instanceof Map<?, ?> map) {
            return map.entrySet().stream().collect(java.util.stream.Collectors.toMap(
                    entry -> String.valueOf(entry.getKey()), Map.Entry::getValue
            ));
        }
        if (claim instanceof String json) {
            return objectMapper.readValue(json, new TypeReference<>() { });
        }
        throw new InvalidExternalIdentityTokenException();
    }
}

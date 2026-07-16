package com.yomora.identity.application;

import com.yomora.identity.domain.ExternalProvider;

public record VerifiedExternalIdentity(
        ExternalProvider provider,
        String subject,
        String email,
        boolean emailVerified,
        String refreshToken
) {
}

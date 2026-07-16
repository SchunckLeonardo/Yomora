package com.yomora.identity.application;

import com.yomora.identity.domain.ExternalProvider;

public record ExternalSignInCommand(
        ExternalProvider provider,
        String identityToken,
        String authorizationCode,
        String nonce,
        String fullName
) {
}

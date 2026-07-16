package com.yomora.identity.infrastructure.security;

import com.yomora.identity.application.ExternalIdentityAuthenticator;
import com.yomora.identity.application.ExternalSignInCommand;
import com.yomora.identity.application.VerifiedExternalIdentity;
import com.yomora.identity.domain.ExternalProvider;

final class AppleExternalIdentityAuthenticator implements ExternalIdentityAuthenticator {
    private final AppleIdentityTokenVerifier tokenVerifier;
    private final AppleAuthorizationCodeClient tokenClient;

    AppleExternalIdentityAuthenticator(
            AppleIdentityTokenVerifier tokenVerifier,
            AppleAuthorizationCodeClient tokenClient
    ) {
        this.tokenVerifier = tokenVerifier;
        this.tokenClient = tokenClient;
    }

    @Override
    public VerifiedExternalIdentity verify(ExternalSignInCommand command) {
        if (command.provider() != ExternalProvider.APPLE) {
            throw new IllegalArgumentException("Provedor de identidade não suportado");
        }
        AppleIdentityClaims claims = tokenVerifier.verify(command.identityToken(), command.nonce());
        String refreshToken = tokenClient.exchange(command.authorizationCode());
        return new VerifiedExternalIdentity(
                ExternalProvider.APPLE,
                claims.subject(),
                claims.email(),
                claims.emailVerified(),
                refreshToken
        );
    }
}

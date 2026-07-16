package com.yomora.identity.infrastructure.security;

interface AppleIdentityTokenVerifier {
    AppleIdentityClaims verify(String identityToken, String rawNonce);
}

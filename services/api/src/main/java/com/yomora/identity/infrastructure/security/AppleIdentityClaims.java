package com.yomora.identity.infrastructure.security;

record AppleIdentityClaims(String subject, String email, boolean emailVerified) {
}

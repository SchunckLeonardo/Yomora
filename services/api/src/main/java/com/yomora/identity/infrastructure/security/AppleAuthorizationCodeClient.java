package com.yomora.identity.infrastructure.security;

interface AppleAuthorizationCodeClient {
    String exchange(String authorizationCode);
}

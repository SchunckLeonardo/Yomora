package com.yomora.identity.application;

public record TokenPair(String accessToken, String refreshToken, String tokenType, long expiresIn) {
}

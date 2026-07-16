package com.yomora.identity.application;

@FunctionalInterface
public interface IdentitySecretCipher {
    String encrypt(String value);

    default String decrypt(String value) {
        throw new UnsupportedOperationException("Descriptografia indisponível");
    }
}

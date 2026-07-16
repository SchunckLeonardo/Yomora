package com.yomora.identity.application;

public class InvalidEmailVerificationTokenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidEmailVerificationTokenException() {
        super("Link de verificação inválido ou expirado");
    }
}

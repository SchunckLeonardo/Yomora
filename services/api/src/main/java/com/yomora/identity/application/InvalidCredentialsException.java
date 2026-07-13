package com.yomora.identity.application;

public final class InvalidCredentialsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidCredentialsException() {
        super("E-mail ou senha inválidos");
    }
}

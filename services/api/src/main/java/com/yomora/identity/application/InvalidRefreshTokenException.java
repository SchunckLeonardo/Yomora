package com.yomora.identity.application;

public final class InvalidRefreshTokenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidRefreshTokenException() {
        super("Refresh token inválido ou expirado");
    }
}

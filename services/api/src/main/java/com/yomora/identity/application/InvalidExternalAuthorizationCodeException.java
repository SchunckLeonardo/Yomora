package com.yomora.identity.application;

public class InvalidExternalAuthorizationCodeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidExternalAuthorizationCodeException() {
        super("O código de autorização do provedor é inválido ou expirou");
    }

    public InvalidExternalAuthorizationCodeException(Throwable cause) {
        super("O código de autorização do provedor é inválido ou expirou", cause);
    }
}

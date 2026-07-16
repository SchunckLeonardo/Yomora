package com.yomora.identity.application;

public class InvalidExternalIdentityTokenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidExternalIdentityTokenException() {
        super("A credencial do provedor de identidade é inválida");
    }

    public InvalidExternalIdentityTokenException(Throwable cause) {
        super("A credencial do provedor de identidade é inválida", cause);
    }
}

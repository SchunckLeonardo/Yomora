package com.yomora.identity.application;

public class ExternalTokenRevocationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ExternalTokenRevocationException(Throwable cause) {
        super("Não foi possível revogar a credencial no provedor de identidade", cause);
    }
}

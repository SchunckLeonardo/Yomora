package com.yomora.identity.application;

public class ExternalAuthenticationUnavailableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ExternalAuthenticationUnavailableException() {
        super("Login externo não configurado");
    }
}

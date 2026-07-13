package com.yomora.catalog.application;

public final class BookProviderUnavailableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BookProviderUnavailableException(String provider) {
        super("Provedor indisponível: " + provider);
    }

    public BookProviderUnavailableException(String provider, Throwable cause) {
        super("Provedor indisponível: " + provider, cause);
    }
}

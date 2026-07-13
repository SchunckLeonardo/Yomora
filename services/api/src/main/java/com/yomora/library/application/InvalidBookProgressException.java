package com.yomora.library.application;

public final class InvalidBookProgressException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidBookProgressException(String message) {
        super(message);
    }
}

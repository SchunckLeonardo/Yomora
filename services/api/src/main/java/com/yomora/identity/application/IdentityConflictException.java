package com.yomora.identity.application;

public final class IdentityConflictException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IdentityConflictException(String message) {
        super(message);
    }
}

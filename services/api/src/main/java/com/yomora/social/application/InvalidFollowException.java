package com.yomora.social.application;

public final class InvalidFollowException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidFollowException(String message) {
        super(message);
    }
}

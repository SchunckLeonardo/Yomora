package com.yomora.library.application;

public final class LibraryConflictException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LibraryConflictException(String message) {
        super(message);
    }
}

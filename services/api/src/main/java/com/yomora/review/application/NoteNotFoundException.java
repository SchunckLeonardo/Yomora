package com.yomora.review.application;

public final class NoteNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoteNotFoundException() {
        super("Nota não encontrada");
    }
}

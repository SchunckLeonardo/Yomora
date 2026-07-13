package com.yomora.library.application;

public final class LibraryEntryNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LibraryEntryNotFoundException() {
        super("Livro não encontrado na biblioteca");
    }
}

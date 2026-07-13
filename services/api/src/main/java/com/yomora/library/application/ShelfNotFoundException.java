package com.yomora.library.application;

public final class ShelfNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ShelfNotFoundException() {
        super("Estante não encontrada");
    }
}

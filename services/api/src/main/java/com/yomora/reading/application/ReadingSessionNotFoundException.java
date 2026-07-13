package com.yomora.reading.application;

public final class ReadingSessionNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ReadingSessionNotFoundException() {
        super("Sessão de leitura não encontrada");
    }
}

package com.yomora.reading.application;

import java.util.UUID;

public final class ActiveReadingSessionExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final UUID activeSessionId;

    public ActiveReadingSessionExistsException(UUID activeSessionId) {
        super("Já existe uma sessão de leitura ativa: " + activeSessionId);
        this.activeSessionId = activeSessionId;
    }

    public UUID activeSessionId() {
        return activeSessionId;
    }
}

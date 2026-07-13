package com.yomora.identity.web;

import java.util.UUID;

public final class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(UUID id) {
        super("Usuário não encontrado: " + id);
    }
}

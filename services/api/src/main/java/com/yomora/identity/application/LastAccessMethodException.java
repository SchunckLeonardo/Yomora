package com.yomora.identity.application;

public class LastAccessMethodException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LastAccessMethodException() {
        super("Adicione uma senha antes de desvincular seu único método de acesso");
    }
}

package com.yomora.social.application;

public final class ContentForbiddenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ContentForbiddenException() {
        super("Você não pode alterar conteúdo de outro usuário");
    }
}

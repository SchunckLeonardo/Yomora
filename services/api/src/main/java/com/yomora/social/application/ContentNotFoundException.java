package com.yomora.social.application;

public final class ContentNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ContentNotFoundException() {
        super("Publicação ou comentário não encontrado");
    }
}

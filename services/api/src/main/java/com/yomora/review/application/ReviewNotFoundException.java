package com.yomora.review.application;

public final class ReviewNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ReviewNotFoundException() {
        super("Review não encontrada");
    }
}

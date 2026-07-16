package com.yomora.identity.application;

public class IdentityLinkRequiredException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IdentityLinkRequiredException() {
        super("Entre com sua senha antes de vincular esta identidade externa");
    }
}

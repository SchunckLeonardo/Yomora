package com.yomora.identity.application;

public interface ExternalIdentityAuthenticator {
    VerifiedExternalIdentity verify(ExternalSignInCommand command);
}

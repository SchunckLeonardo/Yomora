package com.yomora.identity.application;

public interface ExternalAccountEventVerifier {
    ExternalAccountEvent verify(String signedPayload);
}

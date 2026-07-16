package com.yomora.identity.application;

import java.util.Locale;

public enum ExternalAccountEventType {
    CONSENT_REVOKED(true),
    ACCOUNT_DELETE(true),
    EMAIL_DISABLED(false),
    EMAIL_ENABLED(false);

    private final boolean revokesAccess;

    ExternalAccountEventType(boolean revokesAccess) {
        this.revokesAccess = revokesAccess;
    }

    public boolean revokesAccess() {
        return revokesAccess;
    }

    public static ExternalAccountEventType fromProviderValue(String value) {
        return valueOf(value.replace('-', '_').toUpperCase(Locale.ROOT));
    }
}

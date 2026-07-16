package com.yomora.identity.application;

import com.yomora.identity.domain.ExternalProvider;

public record ExternalAccountEvent(
        ExternalProvider provider,
        String subject,
        ExternalAccountEventType type
) {
}

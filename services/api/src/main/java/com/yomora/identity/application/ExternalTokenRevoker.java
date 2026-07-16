package com.yomora.identity.application;

import com.yomora.identity.domain.ExternalProvider;

public interface ExternalTokenRevoker {
    void revoke(ExternalProvider provider, String refreshToken);
}

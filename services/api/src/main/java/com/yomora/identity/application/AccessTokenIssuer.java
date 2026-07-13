package com.yomora.identity.application;

import com.yomora.identity.domain.User;

public interface AccessTokenIssuer {
    String issue(User user);

    long expiresInSeconds();
}

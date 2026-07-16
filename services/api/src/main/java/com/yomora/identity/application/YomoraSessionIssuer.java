package com.yomora.identity.application;

import com.yomora.identity.domain.User;

@FunctionalInterface
public interface YomoraSessionIssuer {
    TokenPair issue(User user);
}

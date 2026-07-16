package com.yomora.identity.domain;

import java.util.Optional;

public interface EmailVerificationTokenRepository {
    EmailVerificationToken save(EmailVerificationToken token);

    Optional<EmailVerificationToken> findByHash(String hash);
}

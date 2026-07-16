package com.yomora.identity.application;

import com.yomora.identity.domain.EmailVerificationToken;
import com.yomora.identity.domain.EmailVerificationTokenRepository;
import com.yomora.identity.domain.User;
import com.yomora.identity.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class EmailVerificationService {
    private final UserRepository users;
    private final EmailVerificationTokenRepository tokens;
    private final EmailVerificationNotifier notifier;
    private final Clock clock;
    private final URI publicApiUrl;
    private final SecureRandom secureRandom = new SecureRandom();

    public EmailVerificationService(
            UserRepository users,
            EmailVerificationTokenRepository tokens,
            EmailVerificationNotifier notifier,
            Clock clock,
            @Value("${yomora.public-api-url:http://localhost:8080}") URI publicApiUrl
    ) {
        this.users = users;
        this.tokens = tokens;
        this.notifier = notifier;
        this.clock = clock;
        this.publicApiUrl = publicApiUrl;
    }

    @Transactional
    public void request(UUID userId) {
        User user = users.findById(userId).orElseThrow(InvalidEmailVerificationTokenException::new);
        if (user.emailVerified()) {
            return;
        }
        Instant now = clock.instant();
        String rawToken = randomToken();
        tokens.save(new EmailVerificationToken(
                UUID.randomUUID(), user.id(), hash(rawToken), now.plus(24, ChronoUnit.HOURS), null, now
        ));
        notifier.send(user.email(), verificationLink(rawToken));
    }

    @Transactional
    public void confirm(String rawToken) {
        Instant now = clock.instant();
        EmailVerificationToken token = tokens.findByHash(hash(rawToken))
                .filter(candidate -> candidate.activeAt(now))
                .orElseThrow(InvalidEmailVerificationTokenException::new);
        User user = users.findById(token.userId()).orElseThrow(InvalidEmailVerificationTokenException::new);
        users.save(user.verifyEmailAt(now));
        tokens.save(token.useAt(now));
    }

    private String verificationLink(String rawToken) {
        String base = publicApiUrl.toString().replaceAll("/+$", "");
        return base + "/api/v1/auth/email-verification/confirm?token=" + rawToken;
    }

    private String randomToken() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 indisponível", exception);
        }
    }
}

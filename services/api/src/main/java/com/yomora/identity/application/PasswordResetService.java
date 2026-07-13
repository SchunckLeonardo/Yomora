package com.yomora.identity.application;

import com.yomora.identity.domain.PasswordResetToken;
import com.yomora.identity.domain.PasswordResetTokenRepository;
import com.yomora.identity.domain.User;
import com.yomora.identity.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;

@Service
public class PasswordResetService {
    private final UserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final PasswordResetNotifier notifier;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(UserRepository users, PasswordResetTokenRepository tokens,
                                PasswordResetNotifier notifier, PasswordEncoder passwordEncoder, Clock clock) {
        this.users = users;
        this.tokens = tokens;
        this.notifier = notifier;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Transactional
    public void request(String email) {
        users.findByEmail(email.trim().toLowerCase(Locale.ROOT)).ifPresent(user -> {
            Instant now = clock.instant();
            String rawToken = randomToken();
            tokens.save(new PasswordResetToken(UUID.randomUUID(), user.id(), hash(rawToken),
                    now.plus(30, ChronoUnit.MINUTES), null, now));
            notifier.send(user.email(), rawToken);
        });
    }

    @Transactional
    public void confirm(String rawToken, String newPassword) {
        Instant now = clock.instant();
        PasswordResetToken token = tokens.findByHash(hash(rawToken))
                .filter(candidate -> candidate.activeAt(now))
                .orElseThrow(InvalidPasswordResetTokenException::new);
        User user = users.findById(token.userId()).orElseThrow(InvalidPasswordResetTokenException::new);
        users.save(new User(user.id(), user.name(), user.username(), user.email(),
                passwordEncoder.encode(newPassword), user.bio(), user.avatarUrl(), user.publicProfile(),
                user.createdAt(), now));
        tokens.save(token.useAt(now));
    }

    private String randomToken() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 indisponível", exception);
        }
    }
}

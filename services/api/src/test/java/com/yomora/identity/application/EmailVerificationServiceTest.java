package com.yomora.identity.application;

import com.yomora.identity.domain.EmailVerificationToken;
import com.yomora.identity.domain.EmailVerificationTokenRepository;
import com.yomora.identity.domain.User;
import com.yomora.identity.domain.UserRepository;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailVerificationServiceTest {
    private static final Instant NOW = Instant.parse("2026-07-16T12:00:00Z");
    private final InMemoryUserRepository users = new InMemoryUserRepository();
    private final InMemoryEmailVerificationTokenRepository tokens = new InMemoryEmailVerificationTokenRepository();
    private final CapturingNotifier notifier = new CapturingNotifier();
    private final EmailVerificationService service = new EmailVerificationService(
            users,
            tokens,
            notifier,
            Clock.fixed(NOW, ZoneOffset.UTC),
            URI.create("https://api.yomora.app")
    );

    @Test
    void sendsAndConfirmsASingleUseVerificationLink() {
        User user = users.save(new User(
                UUID.randomUUID(), "Marina", "marina", "marina@example.com", "hash",
                null, NOW, "", null, true, NOW, NOW
        ));

        service.request(user.id());

        assertThat(notifier.email).isEqualTo("marina@example.com");
        assertThat(notifier.link).startsWith("https://api.yomora.app/api/v1/auth/email-verification/confirm?token=");
        String rawToken = notifier.link.substring(notifier.link.indexOf("token=") + 6);
        assertThat(tokens.values.values()).noneMatch(token -> token.tokenHash().equals(rawToken));

        service.confirm(rawToken);

        assertThat(users.findById(user.id()).orElseThrow().emailVerifiedAt()).isEqualTo(NOW);
        assertThatThrownBy(() -> service.confirm(rawToken))
                .isInstanceOf(InvalidEmailVerificationTokenException.class);
    }

    private static final class CapturingNotifier implements EmailVerificationNotifier {
        private String email;
        private String link;

        @Override
        public void send(String email, String verificationLink) {
            this.email = email;
            this.link = verificationLink;
        }
    }

    private static final class InMemoryEmailVerificationTokenRepository
            implements EmailVerificationTokenRepository {
        private final Map<UUID, EmailVerificationToken> values = new HashMap<>();

        @Override
        public EmailVerificationToken save(EmailVerificationToken token) {
            values.put(token.id(), token);
            return token;
        }

        @Override
        public Optional<EmailVerificationToken> findByHash(String hash) {
            return values.values().stream().filter(token -> token.tokenHash().equals(hash)).findFirst();
        }
    }

    private static final class InMemoryUserRepository implements UserRepository {
        private final Map<UUID, User> values = new HashMap<>();

        @Override public User save(User user) { values.put(user.id(), user); return user; }
        @Override public boolean existsByEmail(String email) { return findByEmail(email).isPresent(); }
        @Override public boolean existsByUsername(String username) { return false; }
        @Override public Optional<User> findByEmail(String email) {
            return values.values().stream().filter(user -> user.email().equalsIgnoreCase(email)).findFirst();
        }
        @Override public Optional<User> findById(UUID id) { return Optional.ofNullable(values.get(id)); }
        @Override public void deleteById(UUID id) { values.remove(id); }
    }
}

package com.yomora.identity.application;

import com.yomora.identity.domain.PasswordResetToken;
import com.yomora.identity.domain.PasswordResetTokenRepository;
import com.yomora.identity.domain.User;
import com.yomora.identity.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordResetServiceTest {
    private static final Instant NOW = Instant.parse("2026-07-13T12:00:00Z");
    private final InMemoryUserRepository users = new InMemoryUserRepository();
    private final InMemoryPasswordResetTokenRepository tokens = new InMemoryPasswordResetTokenRepository();
    private final CapturingNotifier notifier = new CapturingNotifier();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final PasswordResetService service = new PasswordResetService(
            users, tokens, notifier, passwordEncoder, Clock.fixed(NOW, ZoneOffset.UTC)
    );

    @Test
    void issuesAndConsumesASingleUseResetToken() {
        User user = users.save(new User(UUID.randomUUID(), "Marina", "marina", "marina@example.com",
                passwordEncoder.encode("senha-antiga"), NOW, NOW, "", null, true, NOW, NOW));

        service.request(" MARINA@example.com ");
        service.confirm(notifier.token, "uma-nova-senha");

        assertThat(passwordEncoder.matches("uma-nova-senha", users.findById(user.id()).orElseThrow().passwordHash()))
                .isTrue();
        assertThat(tokens.values.values()).allMatch(token -> token.usedAt() != null);
        assertThatThrownBy(() -> service.confirm(notifier.token, "outra-senha"))
                .isInstanceOf(InvalidPasswordResetTokenException.class);
    }

    @Test
    void doesNotRevealWhetherAnEmailExists() {
        service.request("desconhecido@example.com");

        assertThat(notifier.token).isNull();
        assertThat(tokens.values).isEmpty();
    }

    private static final class CapturingNotifier implements PasswordResetNotifier {
        private String token;

        @Override
        public void sendPasswordReset(String email, String rawToken) {
            token = rawToken;
        }
    }

    private static final class InMemoryPasswordResetTokenRepository implements PasswordResetTokenRepository {
        private final Map<UUID, PasswordResetToken> values = new HashMap<>();

        @Override
        public PasswordResetToken save(PasswordResetToken token) {
            values.put(token.id(), token);
            return token;
        }

        @Override
        public Optional<PasswordResetToken> findByHash(String hash) {
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

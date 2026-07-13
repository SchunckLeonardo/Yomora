package com.yomora.identity.application;

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

class UserRegistrationServiceTest {

    private final InMemoryUserRepository repository = new InMemoryUserRepository();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRegistrationService service = new UserRegistrationService(
            repository,
            passwordEncoder,
            Clock.fixed(Instant.parse("2026-07-13T12:00:00Z"), ZoneOffset.UTC)
    );

    @Test
    void registersANormalizedUserWithoutPersistingTheRawPassword() {
        User user = service.register(new RegisterCommand(
                "  Marina Rainha  ",
                "  Marina.Lê  ",
                "  MARINA@EXAMPLE.COM  ",
                "uma-senha-segura"
        ));

        assertThat(user.name()).isEqualTo("Marina Rainha");
        assertThat(user.username()).isEqualTo("marina.lê");
        assertThat(user.email()).isEqualTo("marina@example.com");
        assertThat(user.passwordHash()).isNotEqualTo("uma-senha-segura");
        assertThat(passwordEncoder.matches("uma-senha-segura", user.passwordHash())).isTrue();
        assertThat(user.createdAt()).isEqualTo(Instant.parse("2026-07-13T12:00:00Z"));
    }

    @Test
    void rejectsAnEmailAlreadyRegisteredIgnoringCase() {
        service.register(new RegisterCommand(
                "Marina Rainha",
                "marina",
                "marina@example.com",
                "uma-senha-segura"
        ));

        assertThatThrownBy(() -> service.register(new RegisterCommand(
                "Outra Marina",
                "outra-marina",
                "MARINA@EXAMPLE.COM",
                "outra-senha-segura"
        )))
                .isInstanceOf(IdentityConflictException.class)
                .hasMessage("E-mail já cadastrado");
    }

    private static final class InMemoryUserRepository implements UserRepository {
        private final Map<UUID, User> users = new HashMap<>();

        @Override
        public User save(User user) {
            users.put(user.id(), user);
            return user;
        }

        @Override
        public boolean existsByEmail(String email) {
            return users.values().stream().anyMatch(user -> user.email().equalsIgnoreCase(email));
        }

        @Override
        public boolean existsByUsername(String username) {
            return users.values().stream().anyMatch(user -> user.username().equalsIgnoreCase(username));
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return users.values().stream().filter(user -> user.email().equalsIgnoreCase(email)).findFirst();
        }

        @Override
        public Optional<User> findById(UUID id) {
            return Optional.ofNullable(users.get(id));
        }

        @Override
        public void deleteById(UUID id) {
            users.remove(id);
        }
    }
}

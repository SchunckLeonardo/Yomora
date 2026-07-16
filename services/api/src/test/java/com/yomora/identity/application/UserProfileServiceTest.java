package com.yomora.identity.application;

import com.yomora.identity.domain.User;
import com.yomora.identity.domain.UserRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserProfileServiceTest {
    private static final Instant NOW = Instant.parse("2026-07-13T12:00:00Z");
    private final InMemoryUserRepository repository = new InMemoryUserRepository();
    private final UserProfileService service = new UserProfileService(
            repository,
            Clock.fixed(NOW, ZoneOffset.UTC)
    );

    @Test
    void updatesAndNormalizesTheAuthenticatedProfile() {
        User user = repository.save(user("marina", "marina@example.com"));

        User updated = service.update(user.id(), new UpdateProfileCommand(
                "  Marina Rainha  ", "  Marina.Lê  ", " Leitora constante. ",
                "https://images.example/avatar.jpg", false
        ));

        assertThat(updated.name()).isEqualTo("Marina Rainha");
        assertThat(updated.username()).isEqualTo("marina.lê");
        assertThat(updated.bio()).isEqualTo("Leitora constante.");
        assertThat(updated.publicProfile()).isFalse();
        assertThat(updated.updatedAt()).isEqualTo(NOW);
    }

    @Test
    void refusesAUsernameOwnedByAnotherAccount() {
        User marina = repository.save(user("marina", "marina@example.com"));
        repository.save(user("ana", "ana@example.com"));

        assertThatThrownBy(() -> service.update(marina.id(), new UpdateProfileCommand(
                "Marina", "ANA", "", null, true
        )))
                .isInstanceOf(IdentityConflictException.class)
                .hasMessage("Nome de usuário já cadastrado");
    }

    @Test
    void deletesTheAuthenticatedAccount() {
        User user = repository.save(user("marina", "marina@example.com"));

        service.delete(user.id());

        assertThat(repository.findById(user.id())).isEmpty();
    }

    private User user(String username, String email) {
        return new User(UUID.randomUUID(), "Leitora", username, email, "hash", NOW, NOW,
                "", null, true, NOW.minusSeconds(60), NOW.minusSeconds(60));
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

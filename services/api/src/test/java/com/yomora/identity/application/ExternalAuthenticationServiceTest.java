package com.yomora.identity.application;

import com.yomora.identity.domain.ExternalIdentity;
import com.yomora.identity.domain.ExternalIdentityRepository;
import com.yomora.identity.domain.ExternalProvider;
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

class ExternalAuthenticationServiceTest {
    private static final Instant NOW = Instant.parse("2026-07-16T12:00:00Z");
    private final InMemoryUserRepository users = new InMemoryUserRepository();
    private final InMemoryExternalIdentityRepository identities = new InMemoryExternalIdentityRepository();
    private final StubExternalIdentityAuthenticator authenticator = new StubExternalIdentityAuthenticator();
    private final ExternalAuthenticationService service = new ExternalAuthenticationService(
            users,
            identities,
            authenticator,
            value -> "encrypted:" + value,
            user -> new TokenPair("access:" + user.id(), "refresh", "Bearer", 900),
            Clock.fixed(NOW, ZoneOffset.UTC)
    );

    @Test
    void createsAndAuthenticatesANewAccountFromApple() {
        authenticator.result = new VerifiedExternalIdentity(
                ExternalProvider.APPLE, "apple-subject", "reader@privaterelay.appleid.com", true, "apple-refresh"
        );

        TokenPair pair = service.authenticate(command("Marina Apple"));

        User user = users.values.values().iterator().next();
        assertThat(pair.accessToken()).isEqualTo("access:" + user.id());
        assertThat(user.name()).isEqualTo("Marina Apple");
        assertThat(user.username()).startsWith("leitor-");
        assertThat(user.passwordHash()).isNull();
        assertThat(user.emailVerifiedAt()).isEqualTo(NOW);
        assertThat(user.profileCompletedAt()).isNull();
        assertThat(identities.findByProviderAndSubject(ExternalProvider.APPLE, "apple-subject"))
                .get()
                .satisfies(identity -> {
                    assertThat(identity.userId()).isEqualTo(user.id());
                    assertThat(identity.encryptedRefreshToken()).isEqualTo("encrypted:apple-refresh");
                });
    }

    @Test
    void automaticallyLinksAVerifiedAppleEmailToAVerifiedPasswordAccount() {
        User existing = users.save(new User(
                UUID.randomUUID(), "Marina", "marina", "marina@example.com", "password-hash",
                NOW.minusSeconds(60), NOW.minusSeconds(60), "", null, true,
                NOW.minusSeconds(60), NOW.minusSeconds(60)
        ));
        authenticator.result = new VerifiedExternalIdentity(
                ExternalProvider.APPLE, "linked-subject", "MARINA@example.com", true, "apple-refresh"
        );

        TokenPair pair = service.authenticate(command(null));

        assertThat(pair.accessToken()).isEqualTo("access:" + existing.id());
        assertThat(users.values).hasSize(1);
        assertThat(identities.findByProviderAndSubject(ExternalProvider.APPLE, "linked-subject"))
                .get().extracting(ExternalIdentity::userId).isEqualTo(existing.id());
    }

    @Test
    void refusesAutomaticLinkingWhenTheExistingEmailIsNotVerified() {
        users.save(new User(
                UUID.randomUUID(), "Marina", "marina", "marina@example.com", "password-hash",
                null, NOW, "", null, true, NOW, NOW
        ));
        authenticator.result = new VerifiedExternalIdentity(
                ExternalProvider.APPLE, "unlinked-subject", "marina@example.com", true, "apple-refresh"
        );

        assertThatThrownBy(() -> service.authenticate(command(null)))
                .isInstanceOf(IdentityLinkRequiredException.class);
        assertThat(identities.values).isEmpty();
    }

    private ExternalSignInCommand command(String fullName) {
        return new ExternalSignInCommand(
                ExternalProvider.APPLE, "identity-token", "authorization-code", "raw-nonce", fullName
        );
    }

    private static final class StubExternalIdentityAuthenticator implements ExternalIdentityAuthenticator {
        private VerifiedExternalIdentity result;

        @Override
        public VerifiedExternalIdentity verify(ExternalSignInCommand command) {
            return result;
        }
    }

    private static final class InMemoryExternalIdentityRepository implements ExternalIdentityRepository {
        private final Map<UUID, ExternalIdentity> values = new HashMap<>();

        @Override public ExternalIdentity save(ExternalIdentity identity) {
            values.put(identity.id(), identity);
            return identity;
        }

        @Override public Optional<ExternalIdentity> findByProviderAndSubject(
                ExternalProvider provider, String subject
        ) {
            return values.values().stream()
                    .filter(identity -> identity.provider() == provider && identity.subject().equals(subject))
                    .findFirst();
        }

        @Override public Optional<ExternalIdentity> findByUserIdAndProvider(UUID userId, ExternalProvider provider) {
            return values.values().stream()
                    .filter(identity -> identity.userId().equals(userId) && identity.provider() == provider)
                    .findFirst();
        }

        @Override public java.util.List<ExternalIdentity> findAllByUserId(UUID userId) {
            return values.values().stream().filter(identity -> identity.userId().equals(userId)).toList();
        }

        @Override public void delete(ExternalIdentity identity) { values.remove(identity.id()); }
    }

    private static final class InMemoryUserRepository implements UserRepository {
        private final Map<UUID, User> values = new HashMap<>();

        @Override public User save(User user) { values.put(user.id(), user); return user; }
        @Override public boolean existsByEmail(String email) { return findByEmail(email).isPresent(); }
        @Override public boolean existsByUsername(String username) {
            return values.values().stream().anyMatch(user -> user.username().equalsIgnoreCase(username));
        }
        @Override public Optional<User> findByEmail(String email) {
            return values.values().stream().filter(user -> user.email().equalsIgnoreCase(email)).findFirst();
        }
        @Override public Optional<User> findById(UUID id) { return Optional.ofNullable(values.get(id)); }
        @Override public void deleteById(UUID id) { values.remove(id); }
    }
}

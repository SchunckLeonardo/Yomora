package com.yomora.identity.application;

import com.yomora.identity.domain.ExternalIdentity;
import com.yomora.identity.domain.ExternalIdentityRepository;
import com.yomora.identity.domain.ExternalProvider;
import com.yomora.identity.domain.RefreshToken;
import com.yomora.identity.domain.RefreshTokenRepository;
import com.yomora.identity.domain.User;
import com.yomora.identity.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExternalAccessMethodServiceTest {
    private static final Instant NOW = Instant.parse("2026-07-16T12:00:00Z");
    private final Users users = new Users();
    private final Identities identities = new Identities();
    private final Sessions sessions = new Sessions();
    private final StubAuthenticator authenticator = new StubAuthenticator();
    private final CapturingRevoker revoker = new CapturingRevoker();
    private final ExternalAccessMethodService service = new ExternalAccessMethodService(
            users, identities, sessions, authenticator, new IdentitySecretCipher() {
                @Override public String encrypt(String value) { return "encrypted:" + value; }
                @Override public String decrypt(String value) { return value.substring("encrypted:".length()); }
            },
            new PasswordEncoder() {
                @Override public String encode(CharSequence rawPassword) { return "hash:" + rawPassword; }
                @Override public boolean matches(CharSequence rawPassword, String encodedPassword) { return false; }
            }, revoker, Clock.fixed(NOW, ZoneOffset.UTC)
    );

    @Test
    void appleOnlyAccountCanAddAPasswordAfterFreshAppleReauthentication() {
        User user = users.save(user(null));
        identities.save(identity(user.id(), "subject-1"));
        authenticator.result = verified("subject-1", "new-refresh");

        service.addPassword(user.id(), "new-password", command());

        assertThat(users.findById(user.id()).orElseThrow().passwordHash()).isEqualTo("hash:new-password");
    }

    @Test
    void signedInUserCanExplicitlyLinkAppleAfterFreshAppleAuthentication() {
        User user = users.save(user("password-hash"));
        authenticator.result = verified("new-subject", "new-refresh");

        service.link(user.id(), command());

        assertThat(identities.findByUserIdAndProvider(user.id(), ExternalProvider.APPLE))
                .get()
                .satisfies(identity -> {
                    assertThat(identity.subject()).isEqualTo("new-subject");
                    assertThat(identity.encryptedRefreshToken()).isEqualTo("encrypted:new-refresh");
                });
    }

    @Test
    void cannotUnlinkTheOnlyAccessMethod() {
        User user = users.save(user(null));
        identities.save(identity(user.id(), "subject-1"));
        authenticator.result = verified("subject-1", "new-refresh");

        assertThatThrownBy(() -> service.unlink(user.id(), ExternalProvider.APPLE, command()))
                .isInstanceOf(LastAccessMethodException.class);
    }

    @Test
    void passwordAccountCanUnlinkAppleAfterFreshReauthenticationAndRevokesAppleToken() {
        User user = users.save(user("password-hash"));
        identities.save(identity(user.id(), "subject-1"));
        authenticator.result = verified("subject-1", "new-refresh");

        service.unlink(user.id(), ExternalProvider.APPLE, command());

        assertThat(identities.findByUserIdAndProvider(user.id(), ExternalProvider.APPLE)).isEmpty();
        assertThat(revoker.tokens).containsExactly("apple-refresh", "new-refresh");
    }

    @Test
    void providerRevocationDeletesAppleOnlyAccountButPreservesPasswordAccountAndRevokesSessions() {
        User appleOnly = users.save(user(null));
        User passwordUser = users.save(user("password-hash"));
        identities.save(identity(appleOnly.id(), "apple-only"));
        identities.save(identity(passwordUser.id(), "with-password"));

        service.handleProviderRevocation(ExternalProvider.APPLE, "apple-only");
        service.handleProviderRevocation(ExternalProvider.APPLE, "with-password");

        assertThat(users.findById(appleOnly.id())).isEmpty();
        assertThat(users.findById(passwordUser.id())).isPresent();
        assertThat(identities.findByUserIdAndProvider(passwordUser.id(), ExternalProvider.APPLE)).isEmpty();
        assertThat(sessions.revokedUsers).containsExactlyInAnyOrder(appleOnly.id(), passwordUser.id());
    }

    private User user(String passwordHash) {
        UUID id = UUID.randomUUID();
        return new User(id, "Marina", "reader-" + id.toString().substring(0, 6),
                id + "@example.com", passwordHash, NOW, NOW, "", null, true, NOW, NOW);
    }

    private ExternalIdentity identity(UUID userId, String subject) {
        return new ExternalIdentity(UUID.randomUUID(), userId, ExternalProvider.APPLE, subject,
                "reader@example.com", "encrypted:apple-refresh", NOW, NOW);
    }

    private VerifiedExternalIdentity verified(String subject, String refreshToken) {
        return new VerifiedExternalIdentity(
                ExternalProvider.APPLE, subject, "reader@example.com", true, refreshToken
        );
    }

    private ExternalSignInCommand command() {
        return new ExternalSignInCommand(ExternalProvider.APPLE, "identity", "code", "nonce", null);
    }

    private static final class StubAuthenticator implements ExternalIdentityAuthenticator {
        private VerifiedExternalIdentity result;
        @Override public VerifiedExternalIdentity verify(ExternalSignInCommand command) { return result; }
    }

    private static final class CapturingRevoker implements ExternalTokenRevoker {
        private final List<String> tokens = new ArrayList<>();
        @Override public void revoke(ExternalProvider provider, String refreshToken) { tokens.add(refreshToken); }
    }

    private static final class Users implements UserRepository {
        private final Map<UUID, User> values = new HashMap<>();
        @Override public User save(User user) { values.put(user.id(), user); return user; }
        @Override public boolean existsByEmail(String email) { return findByEmail(email).isPresent(); }
        @Override public boolean existsByUsername(String username) { return false; }
        @Override public Optional<User> findByEmail(String email) { return values.values().stream().filter(user -> user.email().equalsIgnoreCase(email)).findFirst(); }
        @Override public Optional<User> findById(UUID id) { return Optional.ofNullable(values.get(id)); }
        @Override public void deleteById(UUID id) { values.remove(id); }
    }

    private static final class Identities implements ExternalIdentityRepository {
        private final Map<UUID, ExternalIdentity> values = new HashMap<>();
        @Override public ExternalIdentity save(ExternalIdentity identity) { values.put(identity.id(), identity); return identity; }
        @Override public Optional<ExternalIdentity> findByProviderAndSubject(ExternalProvider provider, String subject) { return values.values().stream().filter(identity -> identity.provider() == provider && identity.subject().equals(subject)).findFirst(); }
        @Override public Optional<ExternalIdentity> findByUserIdAndProvider(UUID userId, ExternalProvider provider) { return values.values().stream().filter(identity -> identity.userId().equals(userId) && identity.provider() == provider).findFirst(); }
        @Override public List<ExternalIdentity> findAllByUserId(UUID userId) { return values.values().stream().filter(identity -> identity.userId().equals(userId)).toList(); }
        @Override public void delete(ExternalIdentity identity) { values.remove(identity.id()); }
    }

    private static final class Sessions implements RefreshTokenRepository {
        private final List<UUID> revokedUsers = new ArrayList<>();
        @Override public RefreshToken save(RefreshToken token) { return token; }
        @Override public Optional<RefreshToken> findByHash(String tokenHash) { return Optional.empty(); }
        @Override public void revokeAllByUserId(UUID userId, Instant revokedAt) { revokedUsers.add(userId); }
    }
}

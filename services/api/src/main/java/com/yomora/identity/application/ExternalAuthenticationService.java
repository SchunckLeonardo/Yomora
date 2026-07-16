package com.yomora.identity.application;

import com.yomora.identity.domain.ExternalIdentity;
import com.yomora.identity.domain.ExternalIdentityRepository;
import com.yomora.identity.domain.User;
import com.yomora.identity.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
public class ExternalAuthenticationService {
    private final UserRepository users;
    private final ExternalIdentityRepository identities;
    private final ExternalIdentityAuthenticator authenticator;
    private final IdentitySecretCipher secretCipher;
    private final YomoraSessionIssuer sessionIssuer;
    private final Clock clock;

    public ExternalAuthenticationService(
            UserRepository users,
            ExternalIdentityRepository identities,
            ExternalIdentityAuthenticator authenticator,
            IdentitySecretCipher secretCipher,
            YomoraSessionIssuer sessionIssuer,
            Clock clock
    ) {
        this.users = users;
        this.identities = identities;
        this.authenticator = authenticator;
        this.secretCipher = secretCipher;
        this.sessionIssuer = sessionIssuer;
        this.clock = clock;
    }

    @Transactional
    public TokenPair authenticate(ExternalSignInCommand command) {
        VerifiedExternalIdentity verified = authenticator.verify(command);
        return identities.findByProviderAndSubject(verified.provider(), verified.subject())
                .map(identity -> authenticateExisting(identity, verified))
                .orElseGet(() -> authenticateFirstUse(command, verified));
    }

    private TokenPair authenticateExisting(ExternalIdentity identity, VerifiedExternalIdentity verified) {
        User user = users.findById(identity.userId()).orElseThrow(InvalidCredentialsException::new);
        identities.save(new ExternalIdentity(
                identity.id(), identity.userId(), identity.provider(), identity.subject(),
                normalizeEmail(verified.email()), secretCipher.encrypt(verified.refreshToken()),
                identity.createdAt(), clock.instant()
        ));
        return sessionIssuer.issue(user);
    }

    private TokenPair authenticateFirstUse(
            ExternalSignInCommand command,
            VerifiedExternalIdentity verified
    ) {
        String normalizedEmail = normalizeEmail(verified.email());
        User user = users.findByEmail(normalizedEmail)
                .map(existing -> linkVerifiedEmail(existing, verified))
                .orElseGet(() -> createUser(command, verified, normalizedEmail));
        saveIdentity(user, verified, normalizedEmail);
        return sessionIssuer.issue(user);
    }

    private User linkVerifiedEmail(User user, VerifiedExternalIdentity verified) {
        if (!user.emailVerified() || !verified.emailVerified()) {
            throw new IdentityLinkRequiredException();
        }
        return user;
    }

    private User createUser(
            ExternalSignInCommand command,
            VerifiedExternalIdentity verified,
            String normalizedEmail
    ) {
        Instant now = clock.instant();
        String fullName = command.fullName() == null || command.fullName().isBlank()
                ? "Leitor Yomora"
                : command.fullName().trim();
        return users.save(new User(
                UUID.randomUUID(), fullName, temporaryUsername(), normalizedEmail, null,
                verified.emailVerified() ? now : null, null, "", null, true, now, now
        ));
    }

    private void saveIdentity(User user, VerifiedExternalIdentity verified, String normalizedEmail) {
        Instant now = clock.instant();
        identities.save(new ExternalIdentity(
                UUID.randomUUID(), user.id(), verified.provider(), verified.subject(), normalizedEmail,
                secretCipher.encrypt(verified.refreshToken()), now, now
        ));
    }

    private String temporaryUsername() {
        String candidate;
        do {
            candidate = "leitor-" + UUID.randomUUID().toString().substring(0, 8);
        } while (users.existsByUsername(candidate));
        return candidate;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

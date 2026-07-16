package com.yomora.identity.application;

import com.yomora.identity.domain.ExternalIdentity;
import com.yomora.identity.domain.ExternalIdentityRepository;
import com.yomora.identity.domain.ExternalProvider;
import com.yomora.identity.domain.RefreshTokenRepository;
import com.yomora.identity.domain.User;
import com.yomora.identity.domain.UserRepository;
import com.yomora.identity.web.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

@Service
public class ExternalAccessMethodService {
    private final UserRepository users;
    private final ExternalIdentityRepository identities;
    private final RefreshTokenRepository sessions;
    private final ExternalIdentityAuthenticator authenticator;
    private final IdentitySecretCipher cipher;
    private final PasswordEncoder passwordEncoder;
    private final ExternalTokenRevoker tokenRevoker;
    private final Clock clock;

    public ExternalAccessMethodService(
            UserRepository users,
            ExternalIdentityRepository identities,
            RefreshTokenRepository sessions,
            ExternalIdentityAuthenticator authenticator,
            IdentitySecretCipher cipher,
            PasswordEncoder passwordEncoder,
            ExternalTokenRevoker tokenRevoker,
            Clock clock
    ) {
        this.users = users;
        this.identities = identities;
        this.sessions = sessions;
        this.authenticator = authenticator;
        this.cipher = cipher;
        this.passwordEncoder = passwordEncoder;
        this.tokenRevoker = tokenRevoker;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public AccessMethods methods(UUID userId) {
        User user = requireUser(userId);
        List<ExternalProvider> providers = identities.findAllByUserId(userId).stream()
                .map(ExternalIdentity::provider)
                .toList();
        return new AccessMethods(user.hasPassword(), providers);
    }

    @Transactional
    public void link(UUID userId, ExternalSignInCommand command) {
        User user = requireUser(userId);
        VerifiedExternalIdentity verified = authenticator.verify(command);
        identities.findByProviderAndSubject(verified.provider(), verified.subject()).ifPresent(existing -> {
            if (!existing.userId().equals(user.id())) {
                throw new IdentityConflictException("Essa identidade já está vinculada a outra conta");
            }
        });
        ExternalIdentity identity = identities.findByUserIdAndProvider(user.id(), verified.provider())
                .orElseGet(() -> new ExternalIdentity(
                        UUID.randomUUID(), user.id(), verified.provider(), verified.subject(), verified.email(),
                        cipher.encrypt(verified.refreshToken()), clock.instant(), clock.instant()
                ));
        if (!identity.subject().equals(verified.subject())) {
            throw new IdentityConflictException("A conta já possui outra identidade desse provedor");
        }
        identities.save(new ExternalIdentity(
                identity.id(), identity.userId(), identity.provider(), identity.subject(), verified.email(),
                cipher.encrypt(verified.refreshToken()), identity.createdAt(), clock.instant()
        ));
    }

    @Transactional
    public void addPassword(UUID userId, String password, ExternalSignInCommand reauthentication) {
        User user = requireUser(userId);
        LinkedVerification verification = verifyLinkedIdentity(userId, reauthentication);
        ExternalIdentity linked = verification.identity();
        VerifiedExternalIdentity verified = verification.verified();
        users.save(new User(
                user.id(), user.name(), user.username(), user.email(), passwordEncoder.encode(password),
                user.emailVerifiedAt(), user.profileCompletedAt(), user.bio(), user.avatarUrl(),
                user.publicProfile(), user.createdAt(), clock.instant()
        ));
        identities.save(new ExternalIdentity(
                linked.id(), linked.userId(), linked.provider(), linked.subject(), linked.email(),
                cipher.encrypt(verified.refreshToken()), linked.createdAt(), clock.instant()
        ));
    }

    @Transactional
    public void unlink(UUID userId, ExternalProvider provider, ExternalSignInCommand reauthentication) {
        User user = requireUser(userId);
        if (!user.hasPassword()) {
            throw new LastAccessMethodException();
        }
        LinkedVerification verification = verifyLinkedIdentity(userId, reauthentication);
        ExternalIdentity linked = verification.identity();
        VerifiedExternalIdentity verified = verification.verified();
        if (linked.provider() != provider) {
            throw new InvalidExternalIdentityTokenException();
        }
        tokenRevoker.revoke(provider, cipher.decrypt(linked.encryptedRefreshToken()));
        if (!verified.refreshToken().equals(cipher.decrypt(linked.encryptedRefreshToken()))) {
            tokenRevoker.revoke(provider, verified.refreshToken());
        }
        identities.delete(linked);
    }

    @Transactional
    public void handleProviderRevocation(ExternalProvider provider, String subject) {
        identities.findByProviderAndSubject(provider, subject).ifPresent(identity -> {
            User user = requireUser(identity.userId());
            sessions.revokeAllByUserId(user.id(), clock.instant());
            identities.delete(identity);
            if (!user.hasPassword() && identities.findAllByUserId(user.id()).isEmpty()) {
                users.deleteById(user.id());
            }
        });
    }

    @Transactional
    public void deleteAccount(UUID userId) {
        requireUser(userId);
        identities.findAllByUserId(userId).forEach(identity ->
                tokenRevoker.revoke(identity.provider(), cipher.decrypt(identity.encryptedRefreshToken()))
        );
        users.deleteById(userId);
    }

    private LinkedVerification verifyLinkedIdentity(UUID userId, ExternalSignInCommand command) {
        VerifiedExternalIdentity verified = authenticator.verify(command);
        ExternalIdentity identity = identities.findByProviderAndSubject(verified.provider(), verified.subject())
                .orElseThrow(InvalidExternalIdentityTokenException::new);
        if (!identity.userId().equals(userId)) {
            throw new InvalidExternalIdentityTokenException();
        }
        return new LinkedVerification(identity, verified);
    }

    private User requireUser(UUID userId) {
        return users.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    public record AccessMethods(boolean password, List<ExternalProvider> providers) {
    }

    private record LinkedVerification(ExternalIdentity identity, VerifiedExternalIdentity verified) {
    }
}

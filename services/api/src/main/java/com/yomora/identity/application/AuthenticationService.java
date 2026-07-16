package com.yomora.identity.application;

import com.yomora.identity.domain.RefreshToken;
import com.yomora.identity.domain.RefreshTokenRepository;
import com.yomora.identity.domain.User;
import com.yomora.identity.domain.UserRepository;
import com.yomora.shared.config.SecurityProperties;
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
import java.util.UUID;

@Service
public class AuthenticationService implements YomoraSessionIssuer {
    private final UserRegistrationService registrationService;
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenIssuer accessTokenIssuer;
    private final SecurityProperties properties;
    private final Clock clock;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthenticationService(
            UserRegistrationService registrationService,
            UserRepository userRepository,
            EmailVerificationService emailVerificationService,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            AccessTokenIssuer accessTokenIssuer,
            SecurityProperties properties,
            Clock clock
    ) {
        this.registrationService = registrationService;
        this.userRepository = userRepository;
        this.emailVerificationService = emailVerificationService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.accessTokenIssuer = accessTokenIssuer;
        this.properties = properties;
        this.clock = clock;
    }

    @Transactional
    public TokenPair register(RegisterCommand command) {
        User user = registrationService.register(command);
        emailVerificationService.request(user.id());
        return issue(user);
    }

    @Transactional
    public TokenPair login(String email, String password) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .filter(User::hasPassword)
                .filter(candidate -> passwordEncoder.matches(password, candidate.passwordHash()))
                .orElseThrow(InvalidCredentialsException::new);
        return issue(user);
    }

    @Transactional
    public TokenPair refresh(String refreshToken) {
        Instant now = clock.instant();
        RefreshToken stored = refreshTokenRepository.findByHash(hash(refreshToken))
                .filter(token -> token.activeAt(now))
                .orElseThrow(InvalidRefreshTokenException::new);
        refreshTokenRepository.save(stored.revokeAt(now));
        User user = userRepository.findById(stored.userId()).orElseThrow(InvalidRefreshTokenException::new);
        return issue(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        Instant now = clock.instant();
        refreshTokenRepository.findByHash(hash(refreshToken))
                .filter(token -> token.revokedAt() == null)
                .ifPresent(token -> refreshTokenRepository.save(token.revokeAt(now)));
    }

    @Override
    @Transactional
    public TokenPair issue(User user) {
        Instant now = clock.instant();
        String plainRefreshToken = randomToken();
        refreshTokenRepository.save(new RefreshToken(
                UUID.randomUUID(),
                user.id(),
                hash(plainRefreshToken),
                now.plus(properties.refreshTokenDays(), ChronoUnit.DAYS),
                null,
                now
        ));
        return new TokenPair(
                accessTokenIssuer.issue(user),
                plainRefreshToken,
                "Bearer",
                accessTokenIssuer.expiresInSeconds()
        );
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

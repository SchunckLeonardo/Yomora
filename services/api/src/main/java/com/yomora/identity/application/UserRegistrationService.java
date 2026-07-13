package com.yomora.identity.application;

import com.yomora.identity.domain.User;
import com.yomora.identity.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

public final class UserRegistrationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public UserRegistrationService(UserRepository repository, PasswordEncoder passwordEncoder, Clock clock) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    public User register(RegisterCommand command) {
        String email = normalizeIdentity(command.email());
        String username = normalizeIdentity(command.username());

        if (repository.existsByEmail(email)) {
            throw new IdentityConflictException("E-mail já cadastrado");
        }
        if (repository.existsByUsername(username)) {
            throw new IdentityConflictException("Nome de usuário já cadastrado");
        }

        Instant now = clock.instant();
        return repository.save(new User(
                UUID.randomUUID(),
                command.name().trim(),
                username,
                email,
                passwordEncoder.encode(command.password()),
                "",
                null,
                true,
                now,
                now
        ));
    }

    private String normalizeIdentity(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}

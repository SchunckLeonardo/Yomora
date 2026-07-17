package com.yomora.identity.application;

import com.yomora.identity.domain.User;
import com.yomora.identity.domain.UserRepository;
import com.yomora.identity.web.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Locale;
import java.util.UUID;

@Service
public class UserProfileService {
    private final UserRepository repository;
    private final Clock clock;

    public UserProfileService(UserRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public User update(UUID userId, UpdateProfileCommand command) {
        User current = repository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        String username = command.username().trim().toLowerCase(Locale.ROOT);
        if (!current.username().equalsIgnoreCase(username) && repository.existsByUsername(username)) {
            throw new IdentityConflictException("Nome de usuário já cadastrado");
        }
        return repository.save(new User(
                current.id(),
                command.name().trim(),
                username,
                current.email(),
                current.passwordHash(),
                command.bio() == null ? "" : command.bio().trim(),
                current.avatarUrl(),
                command.publicProfile(),
                current.createdAt(),
                clock.instant()
        ));
    }

    @Transactional
    public void delete(UUID userId) {
        if (repository.findById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        repository.deleteById(userId);
    }

}

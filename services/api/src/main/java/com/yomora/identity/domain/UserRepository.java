package com.yomora.identity.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);

    void deleteById(UUID id);
}

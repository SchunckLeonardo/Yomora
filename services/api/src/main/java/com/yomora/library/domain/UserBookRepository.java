package com.yomora.library.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserBookRepository {
    UserBook save(UserBook userBook);

    boolean exists(UUID userId, UUID editionId);

    Optional<UserBook> findOwned(UUID id, UUID userId);

    List<UserBook> list(UUID userId, ReadingStatus status);

    void delete(UserBook userBook);
}

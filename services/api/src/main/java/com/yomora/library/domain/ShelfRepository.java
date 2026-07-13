package com.yomora.library.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShelfRepository {
    Shelf save(Shelf shelf);

    Optional<Shelf> findOwned(UUID id, UUID userId);

    List<Shelf> list(UUID userId);

    void delete(Shelf shelf);
}

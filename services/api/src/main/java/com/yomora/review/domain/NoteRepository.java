package com.yomora.review.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository {
    Note save(Note note);

    Optional<Note> findOwned(UUID id, UUID userId);

    List<Note> list(UUID userId, UUID editionId);

    void delete(Note note);
}

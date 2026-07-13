package com.yomora.review.infrastructure.persistence;

import com.yomora.review.domain.Note;
import com.yomora.review.domain.NoteRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaNoteRepository implements NoteRepository {
    private final SpringDataNoteRepository repository;

    JpaNoteRepository(SpringDataNoteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Note save(Note note) {
        return repository.save(NoteEntity.from(note)).toDomain();
    }

    @Override
    public Optional<Note> findOwned(UUID id, UUID userId) {
        return repository.findByIdAndUserId(id, userId).map(NoteEntity::toDomain);
    }

    @Override
    public List<Note> list(UUID userId, UUID editionId) {
        return repository.findAllByUserIdAndEditionIdOrderByCreatedAtDesc(userId, editionId).stream()
                .map(NoteEntity::toDomain)
                .toList();
    }

    @Override
    public void delete(Note note) {
        repository.deleteById(note.id());
    }
}

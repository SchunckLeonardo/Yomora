package com.yomora.review.application;

import com.yomora.review.domain.Note;
import com.yomora.review.domain.NoteRepository;
import com.yomora.social.domain.PostType;
import com.yomora.social.domain.Visibility;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class NoteService {
    private final NoteRepository repository;
    private final SocialPublisher publisher;
    private final Clock clock;

    public NoteService(NoteRepository repository, SocialPublisher publisher, Clock clock) {
        this.repository = repository;
        this.publisher = publisher;
        this.clock = clock;
    }

    @Transactional
    public Note create(
            UUID userId,
            UUID editionId,
            String content,
            Integer page,
            String chapter,
            boolean privateNote,
            boolean spoiler
    ) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("O conteúdo da nota é obrigatório");
        }
        Instant now = clock.instant();
        UUID postId = privateNote ? null : publisher.upsert(
                userId, null, editionId, content.trim(), PostType.NOTE, spoiler, Visibility.PUBLIC
        );
        return repository.save(new Note(
                UUID.randomUUID(), userId, editionId, content.trim(), page,
                chapter == null || chapter.isBlank() ? null : chapter.trim(),
                privateNote, spoiler, postId, now, now
        ));
    }

    @Transactional(readOnly = true)
    public List<Note> list(UUID userId, UUID editionId) {
        return repository.list(userId, editionId);
    }

    @Transactional
    public void delete(UUID userId, UUID noteId) {
        Note note = repository.findOwned(noteId, userId).orElseThrow(NoteNotFoundException::new);
        if (note.postId() != null) {
            publisher.delete(userId, note.postId());
        }
        repository.delete(note);
    }
}

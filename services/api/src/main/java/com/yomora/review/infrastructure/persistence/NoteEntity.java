package com.yomora.review.infrastructure.persistence;

import com.yomora.review.domain.Note;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notes")
class NoteEntity {
    @Id
    UUID id;
    @Column(name = "user_id")
    UUID userId;
    @Column(name = "edition_id")
    UUID editionId;
    String content;
    Integer page;
    String chapter;
    @Column(name = "private_note")
    boolean privateNote;
    boolean spoiler;
    @Column(name = "post_id")
    UUID postId;
    @Column(name = "created_at")
    Instant createdAt;
    @Column(name = "updated_at")
    Instant updatedAt;

    protected NoteEntity() {
    }

    private NoteEntity(Note note) {
        this.id = note.id();
        this.userId = note.userId();
        this.editionId = note.editionId();
        this.content = note.content();
        this.page = note.page();
        this.chapter = note.chapter();
        this.privateNote = note.privateNote();
        this.spoiler = note.spoiler();
        this.postId = note.postId();
        this.createdAt = note.createdAt();
        this.updatedAt = note.updatedAt();
    }

    static NoteEntity from(Note note) {
        return new NoteEntity(note);
    }

    Note toDomain() {
        return new Note(
                id, userId, editionId, content, page, chapter, privateNote, spoiler, postId, createdAt, updatedAt
        );
    }
}

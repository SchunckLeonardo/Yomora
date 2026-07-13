package com.yomora.reading.infrastructure.persistence;

import com.yomora.reading.domain.ReadingSession;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reading_sessions")
class ReadingSessionEntity {
    @Id
    UUID id;
    @Column(name = "user_id")
    UUID userId;
    @Column(name = "user_book_id")
    UUID userBookId;
    @Column(name = "start_page")
    int startPage;
    @Column(name = "end_page")
    Integer endPage;
    @Column(name = "goal_pages")
    Integer goalPages;
    @Column(name = "started_at")
    Instant startedAt;
    @Column(name = "finished_at")
    Instant finishedAt;
    @Column(name = "duration_seconds")
    Long durationSeconds;
    @Column(name = "pages_read")
    Integer pagesRead;
    String note;

    protected ReadingSessionEntity() {
    }

    private ReadingSessionEntity(ReadingSession session) {
        this.id = session.id();
        this.userId = session.userId();
        this.userBookId = session.userBookId();
        this.startPage = session.startPage();
        this.endPage = session.endPage();
        this.goalPages = session.goalPages();
        this.startedAt = session.startedAt();
        this.finishedAt = session.finishedAt();
        this.durationSeconds = session.durationSeconds();
        this.pagesRead = session.pagesRead();
        this.note = session.note();
    }

    static ReadingSessionEntity from(ReadingSession session) {
        return new ReadingSessionEntity(session);
    }

    ReadingSession toDomain() {
        return new ReadingSession(
                id, userId, userBookId, startPage, endPage, goalPages,
                startedAt, finishedAt, durationSeconds, pagesRead, note
        );
    }
}

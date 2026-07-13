package com.yomora.library.infrastructure.persistence;

import com.yomora.library.domain.ReadingStatus;
import com.yomora.library.domain.UserBook;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_books")
class UserBookEntity {
    @Id
    UUID id;
    @Column(name = "user_id")
    UUID userId;
    @Column(name = "edition_id")
    UUID editionId;
    @Enumerated(EnumType.STRING)
    ReadingStatus status;
    @Column(name = "current_page")
    int currentPage;
    @Column(name = "started_at")
    Instant startedAt;
    @Column(name = "finished_at")
    Instant finishedAt;
    Integer rating;
    @Column(name = "target_finish_date")
    LocalDate targetFinishDate;
    @Column(name = "created_at")
    Instant createdAt;
    @Column(name = "updated_at")
    Instant updatedAt;

    protected UserBookEntity() {
    }

    private UserBookEntity(UserBook book) {
        this.id = book.id();
        this.userId = book.userId();
        this.editionId = book.editionId();
        this.status = book.status();
        this.currentPage = book.currentPage();
        this.startedAt = book.startedAt();
        this.finishedAt = book.finishedAt();
        this.rating = book.rating();
        this.targetFinishDate = book.targetFinishDate();
        this.createdAt = book.createdAt();
        this.updatedAt = book.updatedAt();
    }

    static UserBookEntity from(UserBook book) {
        return new UserBookEntity(book);
    }

    UserBook toDomain() {
        return new UserBook(
                id, userId, editionId, status, currentPage, startedAt, finishedAt,
                rating, targetFinishDate, createdAt, updatedAt
        );
    }
}

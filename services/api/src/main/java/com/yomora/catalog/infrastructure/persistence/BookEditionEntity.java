package com.yomora.catalog.infrastructure.persistence;

import com.yomora.catalog.domain.BookSearchResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "book_editions")
class BookEditionEntity {
    @Id
    UUID id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "work_id")
    BookWorkEntity work;
    String isbn10;
    String isbn13;
    String publisher;
    @Column(name = "publication_date")
    LocalDate publicationDate;
    String language;
    @Column(name = "page_count")
    Integer pageCount;
    @Column(name = "cover_url")
    String coverUrl;
    @Column(name = "external_provider")
    String externalProvider;
    @Column(name = "external_id")
    String externalId;
    @Column(name = "created_at")
    Instant createdAt;
    @Column(name = "updated_at")
    Instant updatedAt;

    protected BookEditionEntity() {
    }

    BookEditionEntity(
            UUID id,
            BookWorkEntity work,
            String isbn10,
            String isbn13,
            String publisher,
            LocalDate publicationDate,
            String language,
            Integer pageCount,
            String coverUrl,
            String externalProvider,
            String externalId,
            Instant now
    ) {
        this.id = id;
        this.work = work;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.language = language;
        this.pageCount = pageCount;
        this.coverUrl = coverUrl;
        this.externalProvider = externalProvider;
        this.externalId = externalId;
        this.createdAt = now;
        this.updatedAt = now;
    }

    BookSearchResult toResult() {
        return new BookSearchResult(
                work.id,
                id,
                work.title,
                work.description,
                List.copyOf(work.authors),
                List.copyOf(work.categories),
                isbn10,
                isbn13,
                publisher,
                publicationDate,
                language,
                pageCount,
                coverUrl,
                externalProvider,
                externalId
        );
    }
}

package com.yomora.catalog.infrastructure.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "book_works")
class BookWorkEntity {
    @Id
    UUID id;
    String title;
    @Column(columnDefinition = "text")
    String description;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_authors", joinColumns = @JoinColumn(name = "work_id"))
    @Column(name = "author_name")
    List<String> authors = new ArrayList<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_categories", joinColumns = @JoinColumn(name = "work_id"))
    @Column(name = "category")
    List<String> categories = new ArrayList<>();
    @Column(name = "created_at")
    Instant createdAt;
    @Column(name = "updated_at")
    Instant updatedAt;

    protected BookWorkEntity() {
    }

    BookWorkEntity(UUID id, String title, String description, List<String> authors, List<String> categories, Instant now) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.authors = new ArrayList<>(authors);
        this.categories = new ArrayList<>(categories);
        this.createdAt = now;
        this.updatedAt = now;
    }
}

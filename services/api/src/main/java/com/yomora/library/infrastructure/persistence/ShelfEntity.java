package com.yomora.library.infrastructure.persistence;

import com.yomora.library.domain.Shelf;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "shelves")
class ShelfEntity {
    @Id
    UUID id;
    @Column(name = "user_id")
    UUID userId;
    String name;
    @Column(name = "public_shelf")
    boolean publicShelf;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "shelf_books", joinColumns = @JoinColumn(name = "shelf_id"))
    @Column(name = "user_book_id")
    Set<UUID> userBookIds = new LinkedHashSet<>();
    @Column(name = "created_at")
    Instant createdAt;
    @Column(name = "updated_at")
    Instant updatedAt;

    protected ShelfEntity() {
    }

    private ShelfEntity(Shelf shelf) {
        this.id = shelf.id();
        this.userId = shelf.userId();
        this.name = shelf.name();
        this.publicShelf = shelf.publicShelf();
        this.userBookIds = new LinkedHashSet<>(shelf.userBookIds());
        this.createdAt = shelf.createdAt();
        this.updatedAt = shelf.updatedAt();
    }

    static ShelfEntity from(Shelf shelf) {
        return new ShelfEntity(shelf);
    }

    Shelf toDomain() {
        return new Shelf(id, userId, name, publicShelf, Set.copyOf(userBookIds), createdAt, updatedAt);
    }
}

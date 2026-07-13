package com.yomora.library.application;

import com.yomora.library.domain.Shelf;
import com.yomora.library.domain.ShelfRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ShelfService {
    private final ShelfRepository repository;
    private final Clock clock;

    public ShelfService(ShelfRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public Shelf create(UUID userId, String name, boolean publicShelf) {
        Instant now = clock.instant();
        return repository.save(new Shelf(
                UUID.randomUUID(), userId, normalizeName(name), publicShelf, Set.of(), now, now
        ));
    }

    @Transactional(readOnly = true)
    public Shelf get(UUID userId, UUID shelfId) {
        return repository.findOwned(shelfId, userId).orElseThrow(ShelfNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Shelf> list(UUID userId) {
        return repository.list(userId);
    }

    @Transactional
    public Shelf rename(UUID userId, UUID shelfId, String name, boolean publicShelf) {
        Shelf current = get(userId, shelfId);
        return repository.save(new Shelf(
                current.id(), current.userId(), normalizeName(name), publicShelf,
                current.userBookIds(), current.createdAt(), clock.instant()
        ));
    }

    @Transactional
    public Shelf addBook(UUID userId, UUID shelfId, UUID userBookId) {
        Shelf current = get(userId, shelfId);
        LinkedHashSet<UUID> books = new LinkedHashSet<>(current.userBookIds());
        books.add(userBookId);
        return repository.save(copyWithBooks(current, books));
    }

    @Transactional
    public Shelf removeBook(UUID userId, UUID shelfId, UUID userBookId) {
        Shelf current = get(userId, shelfId);
        LinkedHashSet<UUID> books = new LinkedHashSet<>(current.userBookIds());
        books.remove(userBookId);
        return repository.save(copyWithBooks(current, books));
    }

    @Transactional
    public void delete(UUID userId, UUID shelfId) {
        repository.delete(get(userId, shelfId));
    }

    private Shelf copyWithBooks(Shelf current, Set<UUID> books) {
        return new Shelf(
                current.id(), current.userId(), current.name(), current.publicShelf(),
                Set.copyOf(books), current.createdAt(), clock.instant()
        );
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome da estante é obrigatório");
        }
        return name.trim();
    }
}

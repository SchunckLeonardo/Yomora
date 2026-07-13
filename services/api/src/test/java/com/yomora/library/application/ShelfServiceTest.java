package com.yomora.library.application;

import com.yomora.library.domain.Shelf;
import com.yomora.library.domain.ShelfRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShelfServiceTest {
    @Test
    void keepsShelfMutationsScopedToTheOwnerAndDoesNotDuplicateBooks() {
        UUID owner = UUID.randomUUID();
        UUID otherUser = UUID.randomUUID();
        UUID userBookId = UUID.randomUUID();
        ShelfService service = new ShelfService(
                new InMemoryShelfRepository(),
                Clock.fixed(Instant.parse("2026-07-13T16:00:00Z"), ZoneOffset.UTC)
        );

        Shelf shelf = service.create(owner, "Favoritos", true);
        service.addBook(owner, shelf.id(), userBookId);
        service.addBook(owner, shelf.id(), userBookId);

        assertThat(service.get(owner, shelf.id()).userBookIds()).containsExactly(userBookId);
        assertThatThrownBy(() -> service.rename(otherUser, shelf.id(), "Roubada", false))
                .isInstanceOf(ShelfNotFoundException.class);
    }

    private static final class InMemoryShelfRepository implements ShelfRepository {
        private final Map<UUID, Shelf> shelves = new HashMap<>();

        @Override
        public Shelf save(Shelf shelf) {
            shelves.put(shelf.id(), shelf);
            return shelf;
        }

        @Override
        public Optional<Shelf> findOwned(UUID id, UUID userId) {
            return Optional.ofNullable(shelves.get(id)).filter(shelf -> shelf.userId().equals(userId));
        }

        @Override
        public List<Shelf> list(UUID userId) {
            return shelves.values().stream().filter(shelf -> shelf.userId().equals(userId)).toList();
        }

        @Override
        public void delete(Shelf shelf) {
            shelves.remove(shelf.id());
        }
    }
}

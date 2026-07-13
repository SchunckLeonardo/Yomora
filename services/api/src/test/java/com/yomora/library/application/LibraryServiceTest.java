package com.yomora.library.application;

import com.yomora.catalog.domain.BookCandidate;
import com.yomora.catalog.domain.BookCatalogRepository;
import com.yomora.catalog.domain.BookSearchResult;
import com.yomora.library.domain.ReadingStatus;
import com.yomora.library.domain.UserBook;
import com.yomora.library.domain.UserBookRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LibraryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EDITION_ID = UUID.randomUUID();
    private final InMemoryUserBookRepository repository = new InMemoryUserBookRepository();
    private final LibraryService service = new LibraryService(
            repository,
            new FixedCatalogRepository(edition()),
            Clock.fixed(Instant.parse("2026-07-13T16:00:00Z"), ZoneOffset.UTC)
    );

    @Test
    void addsAnEditionOnlyOnceToTheUsersLibrary() {
        UserBook first = service.add(USER_ID, EDITION_ID, ReadingStatus.WANT_TO_READ, null);

        assertThat(first.status()).isEqualTo(ReadingStatus.WANT_TO_READ);
        assertThat(first.currentPage()).isZero();
        assertThatThrownBy(() -> service.add(USER_ID, EDITION_ID, ReadingStatus.READING, null))
                .isInstanceOf(LibraryConflictException.class)
                .hasMessage("Livro já está na biblioteca");
    }

    @Test
    void rejectsProgressBeyondTheEditionAndSetsFinishedAtWhenFinished() {
        UserBook entry = service.add(USER_ID, EDITION_ID, ReadingStatus.READING, LocalDate.of(2026, 8, 20));

        assertThatThrownBy(() -> service.update(
                USER_ID, entry.id(), ReadingStatus.READING, 301, null, null
        )).isInstanceOf(InvalidBookProgressException.class);

        UserBook finished = service.update(USER_ID, entry.id(), ReadingStatus.FINISHED, 300, 5, null);
        assertThat(finished.finishedAt()).isEqualTo(Instant.parse("2026-07-13T16:00:00Z"));
        assertThat(finished.rating()).isEqualTo(5);
    }

    private static BookSearchResult edition() {
        return new BookSearchResult(
                UUID.randomUUID(), EDITION_ID, "Duna", "", List.of("Frank Herbert"), List.of("Ficção"),
                null, "9780441172719", "Ace", LocalDate.of(1990, 9, 1), "pt", 300,
                null, "manual", "duna-test"
        );
    }

    private static final class FixedCatalogRepository implements BookCatalogRepository {
        private final BookSearchResult edition;

        private FixedCatalogRepository(BookSearchResult edition) {
            this.edition = edition;
        }

        @Override
        public List<BookSearchResult> search(String query, String language, int limit) {
            return List.of(edition);
        }

        @Override
        public BookSearchResult save(BookCandidate candidate) {
            return edition;
        }

        @Override
        public Optional<BookSearchResult> findEdition(UUID editionId) {
            return edition.editionId().equals(editionId) ? Optional.of(edition) : Optional.empty();
        }
    }

    private static final class InMemoryUserBookRepository implements UserBookRepository {
        private final Map<UUID, UserBook> books = new HashMap<>();

        @Override
        public UserBook save(UserBook userBook) {
            books.put(userBook.id(), userBook);
            return userBook;
        }

        @Override
        public boolean exists(UUID userId, UUID editionId) {
            return books.values().stream()
                    .anyMatch(book -> book.userId().equals(userId) && book.editionId().equals(editionId));
        }

        @Override
        public Optional<UserBook> findOwned(UUID id, UUID userId) {
            return Optional.ofNullable(books.get(id)).filter(book -> book.userId().equals(userId));
        }

        @Override
        public List<UserBook> list(UUID userId, ReadingStatus status) {
            return books.values().stream()
                    .filter(book -> book.userId().equals(userId))
                    .filter(book -> status == null || book.status() == status)
                    .toList();
        }

        @Override
        public void delete(UserBook userBook) {
            books.remove(userBook.id());
        }
    }
}

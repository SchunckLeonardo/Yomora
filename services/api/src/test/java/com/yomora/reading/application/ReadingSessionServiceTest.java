package com.yomora.reading.application;

import com.yomora.catalog.domain.BookCandidate;
import com.yomora.catalog.domain.BookCatalogRepository;
import com.yomora.catalog.domain.BookSearchResult;
import com.yomora.library.domain.ReadingStatus;
import com.yomora.library.domain.UserBook;
import com.yomora.library.domain.UserBookRepository;
import com.yomora.reading.domain.ReadingSession;
import com.yomora.reading.domain.ReadingSessionRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReadingSessionServiceTest {
    @Test
    void finishingASessionUpdatesProgressAndReturnsActionableSummary() {
        UUID userId = UUID.randomUUID();
        UUID editionId = UUID.randomUUID();
        UserBook book = new UserBook(
                UUID.randomUUID(), userId, editionId, ReadingStatus.READING, 120,
                Instant.parse("2026-07-01T10:00:00Z"), null, null, null,
                Instant.parse("2026-07-01T10:00:00Z"), Instant.parse("2026-07-01T10:00:00Z")
        );
        InMemoryUserBookRepository books = new InMemoryUserBookRepository(book);
        InMemoryReadingSessionRepository sessions = new InMemoryReadingSessionRepository();
        MutableClock clock = new MutableClock(Instant.parse("2026-07-13T16:00:00Z"));
        ReadingSessionService service = new ReadingSessionService(
                sessions,
                books,
                new FixedCatalogRepository(editionId, 180),
                clock
        );

        ReadingSession started = service.start(userId, book.id(), 120, 20);
        clock.advance(Duration.ofMinutes(25));
        ReadingSessionSummary summary = service.finish(userId, started.id(), 140, "Capítulo excelente");

        assertThat(summary.durationMinutes()).isEqualTo(25);
        assertThat(summary.pagesRead()).isEqualTo(20);
        assertThat(summary.progressPercent()).isEqualTo(77.78);
        assertThat(summary.averagePagesPerHour()).isEqualTo(48.0);
        assertThat(summary.estimatedSessionsRemaining()).isEqualTo(2);
        assertThat(summary.estimatedFinishDate()).isEqualTo(LocalDate.of(2026, 7, 15));
        assertThat(books.book.currentPage()).isEqualTo(140);
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(Instant instant) {
            this.instant = instant;
        }

        void advance(Duration duration) {
            instant = instant.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }

    private static final class InMemoryReadingSessionRepository implements ReadingSessionRepository {
        private final List<ReadingSession> sessions = new ArrayList<>();

        @Override
        public ReadingSession save(ReadingSession session) {
            sessions.removeIf(existing -> existing.id().equals(session.id()));
            sessions.add(session);
            return session;
        }

        @Override
        public Optional<ReadingSession> findOwned(UUID id, UUID userId) {
            return sessions.stream().filter(session -> session.id().equals(id) && session.userId().equals(userId)).findFirst();
        }

        @Override
        public List<ReadingSession> list(UUID userId) {
            return sessions.stream().filter(session -> session.userId().equals(userId)).toList();
        }

        @Override
        public Map<LocalDate, DailyReading> dailyReading(UUID userId, LocalDate since) {
            return Map.of();
        }
    }

    private static final class InMemoryUserBookRepository implements UserBookRepository {
        private UserBook book;

        private InMemoryUserBookRepository(UserBook book) {
            this.book = book;
        }

        @Override
        public UserBook save(UserBook userBook) {
            book = userBook;
            return book;
        }

        @Override
        public boolean exists(UUID userId, UUID editionId) {
            return true;
        }

        @Override
        public Optional<UserBook> findOwned(UUID id, UUID userId) {
            return book.id().equals(id) && book.userId().equals(userId) ? Optional.of(book) : Optional.empty();
        }

        @Override
        public List<UserBook> list(UUID userId, ReadingStatus status) {
            return List.of(book);
        }

        @Override
        public void delete(UserBook userBook) {
        }
    }

    private record FixedCatalogRepository(UUID editionId, int pages) implements BookCatalogRepository {
        @Override
        public List<BookSearchResult> search(String query, String language, int limit) {
            return List.of();
        }

        @Override
        public BookSearchResult save(BookCandidate candidate) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<BookSearchResult> findEdition(UUID id) {
            if (!editionId.equals(id)) {
                return Optional.empty();
            }
            return Optional.of(new BookSearchResult(
                    UUID.randomUUID(), editionId, "O Alquimista", "", List.of("Paulo Coelho"), List.of(),
                    null, null, null, null, "pt", pages, null, "manual", "test"
            ));
        }
    }
}

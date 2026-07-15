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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReadingSessionServiceTest {
    @Test
    void startingASecondSessionReturnsTheExistingActiveSession() {
        UUID userId = UUID.randomUUID();
        UUID editionId = UUID.randomUUID();
        UserBook book = new UserBook(
                UUID.randomUUID(), userId, editionId, ReadingStatus.READING, 30,
                Instant.parse("2026-07-01T10:00:00Z"), null, null, null,
                Instant.parse("2026-07-01T10:00:00Z"), Instant.parse("2026-07-01T10:00:00Z")
        );
        InMemoryReadingSessionRepository sessions = new InMemoryReadingSessionRepository();
        ReadingSessionService service = new ReadingSessionService(
                sessions,
                new InMemoryUserBookRepository(book),
                new FixedCatalogRepository(editionId, 180),
                Clock.fixed(Instant.parse("2026-07-13T16:00:00Z"), ZoneOffset.UTC)
        );

        ReadingSession active = service.start(userId, book.id(), null, null);

        assertThatThrownBy(() -> service.start(userId, book.id(), null, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(active.id().toString());
    }

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

    @Test
    void pauseAndResumeAreIdempotentAndPausedTimeIsExcludedFromTheSummary() {
        UUID userId = UUID.randomUUID();
        UUID editionId = UUID.randomUUID();
        UserBook book = new UserBook(
                UUID.randomUUID(), userId, editionId, ReadingStatus.READING, 50,
                Instant.parse("2026-07-01T10:00:00Z"), null, null, null,
                Instant.parse("2026-07-01T10:00:00Z"), Instant.parse("2026-07-01T10:00:00Z")
        );
        InMemoryReadingSessionRepository sessions = new InMemoryReadingSessionRepository();
        MutableClock clock = new MutableClock(Instant.parse("2026-07-13T16:00:00Z"));
        ReadingSessionService service = new ReadingSessionService(
                sessions,
                new InMemoryUserBookRepository(book),
                new FixedCatalogRepository(editionId, 180),
                clock
        );

        ReadingSession started = service.start(userId, book.id(), null, null);
        clock.advance(Duration.ofMinutes(10));
        ReadingSession paused = service.pause(userId, started.id());
        clock.advance(Duration.ofMinutes(5));
        ReadingSession stillPaused = service.pause(userId, started.id());
        clock.advance(Duration.ofMinutes(15));
        ReadingSession resumed = service.resume(userId, started.id());
        ReadingSession stillRunning = service.resume(userId, started.id());
        clock.advance(Duration.ofMinutes(5));

        ReadingSessionSummary summary = service.finish(userId, started.id(), 65, null);
        ReadingSession finished = sessions.findOwned(started.id(), userId).orElseThrow();

        assertThat(paused.pausedAt()).isEqualTo(Instant.parse("2026-07-13T16:10:00Z"));
        assertThat(stillPaused.pausedAt()).isEqualTo(paused.pausedAt());
        assertThat(resumed.pausedAt()).isNull();
        assertThat(resumed.pausedSeconds()).isEqualTo(20 * 60);
        assertThat(stillRunning).isEqualTo(resumed);
        assertThat(summary.durationMinutes()).isEqualTo(15);
        assertThat(finished.durationSeconds()).isEqualTo(15 * 60);
        assertThat(finished.pausedSeconds()).isEqualTo(20 * 60);
    }

    @Test
    void activeSessionRestoresAndPersistsTheCurrentPage() {
        UUID userId = UUID.randomUUID();
        UUID editionId = UUID.randomUUID();
        UserBook book = new UserBook(
                UUID.randomUUID(), userId, editionId, ReadingStatus.READING, 50,
                Instant.parse("2026-07-01T10:00:00Z"), null, null, null,
                Instant.parse("2026-07-01T10:00:00Z"), Instant.parse("2026-07-01T10:00:00Z")
        );
        InMemoryReadingSessionRepository sessions = new InMemoryReadingSessionRepository();
        ReadingSessionService service = new ReadingSessionService(
                sessions,
                new InMemoryUserBookRepository(book),
                new FixedCatalogRepository(editionId, 180),
                Clock.fixed(Instant.parse("2026-07-13T16:00:00Z"), ZoneOffset.UTC)
        );

        ReadingSession started = service.start(userId, book.id(), null, null);
        ReadingSession updated = service.updateProgress(userId, started.id(), 65);

        assertThat(started.currentPage()).isEqualTo(50);
        assertThat(updated.currentPage()).isEqualTo(65);
        assertThat(service.active(userId)).contains(updated);
    }

    @Test
    void finishingWhilePausedClosesThePauseIntervalAndRejectsForeignOwnership() {
        UUID userId = UUID.randomUUID();
        UUID editionId = UUID.randomUUID();
        UserBook book = new UserBook(
                UUID.randomUUID(), userId, editionId, ReadingStatus.READING, 20,
                Instant.parse("2026-07-01T10:00:00Z"), null, null, null,
                Instant.parse("2026-07-01T10:00:00Z"), Instant.parse("2026-07-01T10:00:00Z")
        );
        InMemoryReadingSessionRepository sessions = new InMemoryReadingSessionRepository();
        MutableClock clock = new MutableClock(Instant.parse("2026-07-13T16:00:00Z"));
        ReadingSessionService service = new ReadingSessionService(
                sessions,
                new InMemoryUserBookRepository(book),
                new FixedCatalogRepository(editionId, 180),
                clock
        );

        ReadingSession started = service.start(userId, book.id(), null, null);
        clock.advance(Duration.ofMinutes(10));
        service.pause(userId, started.id());
        clock.advance(Duration.ofMinutes(20));

        ReadingSessionSummary summary = service.finish(userId, started.id(), 30, null);
        ReadingSession finished = sessions.findOwned(started.id(), userId).orElseThrow();

        assertThat(summary.durationMinutes()).isEqualTo(10);
        assertThat(finished.pausedSeconds()).isEqualTo(20 * 60);
        assertThat(finished.pausedAt()).isNull();
        assertThat(service.active(userId)).isEmpty();
        assertThatThrownBy(() -> service.pause(UUID.randomUUID(), started.id()))
                .isInstanceOf(ReadingSessionNotFoundException.class);
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
        public Optional<ReadingSession> findActive(UUID userId) {
            return sessions.stream()
                    .filter(session -> session.userId().equals(userId) && session.active())
                    .findFirst();
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

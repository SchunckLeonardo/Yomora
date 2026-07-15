package com.yomora.reading.application;

import com.yomora.catalog.domain.BookCatalogRepository;
import com.yomora.catalog.domain.BookSearchResult;
import com.yomora.library.application.InvalidBookProgressException;
import com.yomora.library.application.LibraryEntryNotFoundException;
import com.yomora.library.domain.ReadingStatus;
import com.yomora.library.domain.UserBook;
import com.yomora.library.domain.UserBookRepository;
import com.yomora.reading.domain.ReadingSession;
import com.yomora.reading.domain.ReadingSessionRepository;
import com.yomora.reading.domain.ReadingGoal;
import com.yomora.reading.domain.ReadingGoalRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReadingSessionService {
    private final ReadingSessionRepository repository;
    private final UserBookRepository userBookRepository;
    private final BookCatalogRepository catalogRepository;
    private final Clock clock;
    private final ReadingGoalRepository goalRepository;
    private final ReadingConsistencyCalculator consistencyCalculator;

    public ReadingSessionService(
            ReadingSessionRepository repository,
            UserBookRepository userBookRepository,
            BookCatalogRepository catalogRepository,
            Clock clock
    ) {
        this.repository = repository;
        this.userBookRepository = userBookRepository;
        this.catalogRepository = catalogRepository;
        this.clock = clock;
        this.goalRepository = null;
        this.consistencyCalculator = null;
    }

    public ReadingSessionService(
            ReadingSessionRepository repository,
            UserBookRepository userBookRepository,
            BookCatalogRepository catalogRepository,
            Clock clock,
            ReadingGoalRepository goalRepository,
            ReadingConsistencyCalculator consistencyCalculator
    ) {
        this.repository = repository;
        this.userBookRepository = userBookRepository;
        this.catalogRepository = catalogRepository;
        this.clock = clock;
        this.goalRepository = goalRepository;
        this.consistencyCalculator = consistencyCalculator;
    }

    @Transactional
    public ReadingSession start(UUID userId, UUID userBookId, Integer startPage, Integer goalPages) {
        UserBook book = userBookRepository.findOwned(userBookId, userId).orElseThrow(LibraryEntryNotFoundException::new);
        repository.findActive(userId).ifPresent(active -> {
            throw new ActiveReadingSessionExistsException(active.id());
        });
        int page = startPage == null ? book.currentPage() : startPage;
        if (page < 0) {
            throw new InvalidBookProgressException("Página inicial inválida");
        }
        return repository.save(new ReadingSession(
                UUID.randomUUID(), userId, userBookId, page, page, null, goalPages,
                clock.instant(), null, 0, null, null, null, null
        ));
    }

    @Transactional
    public ReadingSession pause(UUID userId, UUID sessionId) {
        ReadingSession session = activeOwned(userId, sessionId);
        if (session.paused()) {
            return session;
        }
        return repository.save(new ReadingSession(
                session.id(), session.userId(), session.userBookId(), session.startPage(), session.currentPage(),
                session.endPage(),
                session.goalPages(), session.startedAt(), clock.instant(), session.pausedSeconds(),
                session.finishedAt(), session.durationSeconds(), session.pagesRead(), session.note()
        ));
    }

    @Transactional
    public ReadingSession resume(UUID userId, UUID sessionId) {
        ReadingSession session = activeOwned(userId, sessionId);
        if (!session.paused()) {
            return session;
        }
        Instant resumedAt = clock.instant();
        long pausedSeconds = session.pausedSeconds()
                + Math.max(0, Duration.between(session.pausedAt(), resumedAt).toSeconds());
        return repository.save(new ReadingSession(
                session.id(), session.userId(), session.userBookId(), session.startPage(), session.currentPage(),
                session.endPage(),
                session.goalPages(), session.startedAt(), null, pausedSeconds,
                session.finishedAt(), session.durationSeconds(), session.pagesRead(), session.note()
        ));
    }

    @Transactional
    public ReadingSession updateProgress(UUID userId, UUID sessionId, int currentPage) {
        ReadingSession session = activeOwned(userId, sessionId);
        validatePage(userId, session, currentPage);
        if (session.currentPage() == currentPage) {
            return session;
        }
        return repository.save(new ReadingSession(
                session.id(), session.userId(), session.userBookId(), session.startPage(), currentPage,
                session.endPage(), session.goalPages(), session.startedAt(), session.pausedAt(),
                session.pausedSeconds(), session.finishedAt(), session.durationSeconds(), session.pagesRead(),
                session.note()
        ));
    }

    @Transactional(readOnly = true)
    public Optional<ReadingSession> active(UUID userId) {
        return repository.findActive(userId);
    }

    @Transactional
    public ReadingSessionSummary finish(UUID userId, UUID sessionId, int endPage, String note) {
        ReadingSession session = activeOwned(userId, sessionId);
        UserBook book = userBookRepository.findOwned(session.userBookId(), userId)
                .orElseThrow(LibraryEntryNotFoundException::new);
        BookSearchResult edition = catalogRepository.findEdition(book.editionId())
                .orElseThrow(LibraryEntryNotFoundException::new);
        if (endPage < session.startPage() || edition.pageCount() != null && endPage > edition.pageCount()) {
            throw new InvalidBookProgressException("Página final fora dos limites da edição");
        }

        Instant finishedAt = clock.instant();
        long pausedSeconds = session.pausedSeconds();
        if (session.paused()) {
            pausedSeconds += Math.max(0, Duration.between(session.pausedAt(), finishedAt).toSeconds());
        }
        long durationSeconds = Math.max(
                1,
                Duration.between(session.startedAt(), finishedAt).toSeconds() - pausedSeconds
        );
        int pagesRead = endPage - session.startPage();
        ReadingSession finished = repository.save(new ReadingSession(
                session.id(), session.userId(), session.userBookId(), session.startPage(), endPage, endPage,
                session.goalPages(), session.startedAt(), null, pausedSeconds,
                finishedAt, durationSeconds, pagesRead,
                note == null || note.isBlank() ? null : note.trim()
        ));

        ReadingStatus status = edition.pageCount() != null && endPage >= edition.pageCount()
                ? ReadingStatus.FINISHED
                : ReadingStatus.READING;
        Instant finishedBookAt = status == ReadingStatus.FINISHED ? finishedAt : null;
        userBookRepository.save(new UserBook(
                book.id(), book.userId(), book.editionId(), status, endPage,
                book.startedAt() == null ? session.startedAt() : book.startedAt(),
                finishedBookAt, book.rating(), book.targetFinishDate(), book.createdAt(), finishedAt
        ));

        double progress = edition.pageCount() == null ? 0 : round2(endPage * 100.0 / edition.pageCount());
        double pagesPerHour = round2(pagesRead * 3600.0 / durationSeconds);
        int remainingPages = edition.pageCount() == null ? 0 : Math.max(0, edition.pageCount() - endPage);
        int estimatedSessions = pagesRead <= 0 ? 0 : (int) Math.ceil(remainingPages / (double) pagesRead);
        LocalDate finishDate = LocalDate.ofInstant(finishedAt, ZoneOffset.UTC).plusDays(estimatedSessions);
        int currentStreak = 0;
        if (goalRepository != null && consistencyCalculator != null) {
            LocalDate today = LocalDate.ofInstant(finishedAt, ZoneOffset.UTC);
            ReadingGoal goal = goalRepository.findByUserId(userId).orElse(new ReadingGoal(20, 5, null));
            currentStreak = consistencyCalculator.calculate(
                    goal, repository.dailyReading(userId, today.minusDays(29)), today
            ).currentStreak();
        }
        return new ReadingSessionSummary(
                finished.id(), durationSeconds / 60, pagesRead, progress, pagesPerHour,
                estimatedSessions, finishDate, currentStreak
        );
    }

    @Transactional(readOnly = true)
    public List<ReadingSession> list(UUID userId) {
        return repository.list(userId);
    }

    private ReadingSession activeOwned(UUID userId, UUID sessionId) {
        return repository.findOwned(sessionId, userId)
                .filter(ReadingSession::active)
                .orElseThrow(ReadingSessionNotFoundException::new);
    }

    private void validatePage(UUID userId, ReadingSession session, int page) {
        UserBook book = userBookRepository.findOwned(session.userBookId(), userId)
                .orElseThrow(LibraryEntryNotFoundException::new);
        BookSearchResult edition = catalogRepository.findEdition(book.editionId())
                .orElseThrow(LibraryEntryNotFoundException::new);
        if (page < session.startPage() || edition.pageCount() != null && page > edition.pageCount()) {
            throw new InvalidBookProgressException("Página atual fora dos limites da edição");
        }
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

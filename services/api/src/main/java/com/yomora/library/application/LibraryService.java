package com.yomora.library.application;

import com.yomora.catalog.domain.BookCatalogRepository;
import com.yomora.catalog.domain.BookSearchResult;
import com.yomora.library.domain.ReadingStatus;
import com.yomora.library.domain.UserBook;
import com.yomora.library.domain.UserBookRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class LibraryService {
    private final UserBookRepository repository;
    private final BookCatalogRepository catalogRepository;
    private final Clock clock;

    public LibraryService(UserBookRepository repository, BookCatalogRepository catalogRepository, Clock clock) {
        this.repository = repository;
        this.catalogRepository = catalogRepository;
        this.clock = clock;
    }

    @Transactional
    public UserBook add(UUID userId, UUID editionId, ReadingStatus status, LocalDate targetFinishDate) {
        if (repository.exists(userId, editionId)) {
            throw new LibraryConflictException("Livro já está na biblioteca");
        }
        catalogRepository.findEdition(editionId).orElseThrow(LibraryEntryNotFoundException::new);
        Instant now = clock.instant();
        ReadingStatus initialStatus = status == null ? ReadingStatus.WANT_TO_READ : status;
        return repository.save(new UserBook(
                UUID.randomUUID(),
                userId,
                editionId,
                initialStatus,
                0,
                initialStatus == ReadingStatus.READING ? now : null,
                null,
                null,
                targetFinishDate,
                now,
                now
        ));
    }

    @Transactional
    public UserBook update(
            UUID userId,
            UUID entryId,
            ReadingStatus status,
            Integer currentPage,
            Integer rating,
            LocalDate targetFinishDate
    ) {
        UserBook current = repository.findOwned(entryId, userId).orElseThrow(LibraryEntryNotFoundException::new);
        BookSearchResult edition = catalogRepository.findEdition(current.editionId())
                .orElseThrow(LibraryEntryNotFoundException::new);
        int page = currentPage == null ? current.currentPage() : currentPage;
        if (page < 0 || edition.pageCount() != null && page > edition.pageCount()) {
            throw new InvalidBookProgressException("Página atual fora dos limites da edição");
        }
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new InvalidBookProgressException("A avaliação deve estar entre 1 e 5");
        }

        Instant now = clock.instant();
        ReadingStatus nextStatus = status == null ? current.status() : status;
        Instant startedAt = current.startedAt();
        if (startedAt == null && nextStatus == ReadingStatus.READING) {
            startedAt = now;
        }
        Instant finishedAt = nextStatus == ReadingStatus.FINISHED
                ? current.finishedAt() == null ? now : current.finishedAt()
                : null;

        return repository.save(new UserBook(
                current.id(),
                current.userId(),
                current.editionId(),
                nextStatus,
                page,
                startedAt,
                finishedAt,
                rating == null ? current.rating() : rating,
                targetFinishDate == null ? current.targetFinishDate() : targetFinishDate,
                current.createdAt(),
                now
        ));
    }

    @Transactional(readOnly = true)
    public List<UserBook> list(UUID userId, ReadingStatus status) {
        return repository.list(userId, status);
    }

    @Transactional(readOnly = true)
    public UserBook get(UUID userId, UUID entryId) {
        return repository.findOwned(entryId, userId).orElseThrow(LibraryEntryNotFoundException::new);
    }

    @Transactional
    public void remove(UUID userId, UUID entryId) {
        repository.delete(repository.findOwned(entryId, userId).orElseThrow(LibraryEntryNotFoundException::new));
    }
}

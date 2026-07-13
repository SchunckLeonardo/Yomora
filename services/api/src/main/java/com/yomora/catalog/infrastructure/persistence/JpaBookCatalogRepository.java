package com.yomora.catalog.infrastructure.persistence;

import com.yomora.catalog.domain.BookCandidate;
import com.yomora.catalog.domain.BookCatalogRepository;
import com.yomora.catalog.domain.BookSearchResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaBookCatalogRepository implements BookCatalogRepository {
    private static final int CATEGORY_MAX_LENGTH = 120;

    private final SpringDataBookWorkRepository workRepository;
    private final SpringDataBookEditionRepository editionRepository;
    private final Clock clock;

    JpaBookCatalogRepository(
            SpringDataBookWorkRepository workRepository,
            SpringDataBookEditionRepository editionRepository,
            Clock clock
    ) {
        this.workRepository = workRepository;
        this.editionRepository = editionRepository;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookSearchResult> search(String query, String language, int limit) {
        String normalized = BookSearchResult.normalizeIsbn(query);
        return editionRepository.search(query, normalized, language == null ? "" : language, PageRequest.of(0, limit))
                .stream()
                .map(BookEditionEntity::toResult)
                .toList();
    }

    @Override
    @Transactional
    public BookSearchResult save(BookCandidate candidate) {
        Optional<BookEditionEntity> existing = candidate.isbn13() == null
                ? Optional.empty()
                : editionRepository.findByIsbn13(candidate.isbn13());
        if (existing.isEmpty() && candidate.externalProvider() != null && candidate.externalId() != null) {
            existing = editionRepository.findByExternalProviderAndExternalId(
                    candidate.externalProvider(), candidate.externalId()
            );
        }
        if (existing.isPresent()) {
            return existing.get().toResult();
        }

        Instant now = clock.instant();
        BookWorkEntity work = workRepository.save(new BookWorkEntity(
                UUID.randomUUID(),
                candidate.title(),
                candidate.description(),
                candidate.authors(),
                normalizeCategories(candidate.categories()),
                now
        ));
        return editionRepository.save(new BookEditionEntity(
                UUID.randomUUID(),
                work,
                candidate.isbn10(),
                candidate.isbn13(),
                candidate.publisher(),
                candidate.publicationDate(),
                candidate.language(),
                candidate.pageCount(),
                candidate.coverUrl(),
                candidate.externalProvider(),
                candidate.externalId(),
                now
        )).toResult();
    }

    private List<String> normalizeCategories(List<String> categories) {
        if (categories == null) {
            return List.of();
        }
        return categories.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(category -> !category.isBlank())
                .map(category -> truncate(category, CATEGORY_MAX_LENGTH))
                .distinct()
                .toList();
    }

    private String truncate(String value, int maxLength) {
        int codePointCount = value.codePointCount(0, value.length());
        if (codePointCount <= maxLength) {
            return value;
        }
        return value.substring(0, value.offsetByCodePoints(0, maxLength));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookSearchResult> findEdition(UUID editionId) {
        return editionRepository.findById(editionId).map(BookEditionEntity::toResult);
    }
}

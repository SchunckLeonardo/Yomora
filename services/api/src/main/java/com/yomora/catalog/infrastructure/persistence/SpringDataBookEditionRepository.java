package com.yomora.catalog.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataBookEditionRepository extends JpaRepository<BookEditionEntity, UUID> {
    Optional<BookEditionEntity> findByIsbn13(String isbn13);

    Optional<BookEditionEntity> findByExternalProviderAndExternalId(String provider, String externalId);

    @Query("""
            select edition from BookEditionEntity edition
            join edition.work work
            where (:language = '' or edition.language = :language)
              and (lower(work.title) like lower(concat('%', :query, '%'))
                   or exists (
                       select author from BookWorkEntity matchedWork
                       join matchedWork.authors author
                       where matchedWork = work
                         and lower(author) like lower(concat('%', :query, '%'))
                   )
                   or edition.isbn10 = :normalizedQuery
                   or edition.isbn13 = :normalizedQuery)
            order by work.title
            """)
    List<BookEditionEntity> search(
            @Param("query") String query,
            @Param("normalizedQuery") String normalizedQuery,
            @Param("language") String language,
            Pageable pageable
    );
}

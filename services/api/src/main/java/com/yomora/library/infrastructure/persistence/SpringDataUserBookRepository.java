package com.yomora.library.infrastructure.persistence;

import com.yomora.library.domain.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataUserBookRepository extends JpaRepository<UserBookEntity, UUID> {
    boolean existsByUserIdAndEditionId(UUID userId, UUID editionId);

    Optional<UserBookEntity> findByIdAndUserId(UUID id, UUID userId);

    List<UserBookEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    List<UserBookEntity> findAllByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, ReadingStatus status);
}

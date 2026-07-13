package com.yomora.review.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataNoteRepository extends JpaRepository<NoteEntity, UUID> {
    Optional<NoteEntity> findByIdAndUserId(UUID id, UUID userId);

    List<NoteEntity> findAllByUserIdAndEditionIdOrderByCreatedAtDesc(UUID userId, UUID editionId);
}

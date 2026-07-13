package com.yomora.library.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataShelfRepository extends JpaRepository<ShelfEntity, UUID> {
    Optional<ShelfEntity> findByIdAndUserId(UUID id, UUID userId);

    List<ShelfEntity> findAllByUserIdOrderByName(UUID userId);
}

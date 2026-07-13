package com.yomora.catalog.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SpringDataBookWorkRepository extends JpaRepository<BookWorkEntity, UUID> {
}

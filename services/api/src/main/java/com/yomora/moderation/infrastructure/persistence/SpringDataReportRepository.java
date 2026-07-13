package com.yomora.moderation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SpringDataReportRepository extends JpaRepository<ReportEntity, UUID> {
}

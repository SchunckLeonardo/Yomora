package com.yomora.moderation.infrastructure.persistence;

import com.yomora.moderation.domain.Report;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reports")
class ReportEntity {
    @Id
    UUID id;
    @Column(name = "reporter_id")
    UUID reporterId;
    @Column(name = "reported_user_id")
    UUID reportedUserId;
    @Column(name = "post_id")
    UUID postId;
    String reason;
    String details;
    String status;
    @Column(name = "created_at")
    Instant createdAt;

    protected ReportEntity() {
    }

    private ReportEntity(Report report) {
        this.id = report.id();
        this.reporterId = report.reporterId();
        this.reportedUserId = report.reportedUserId();
        this.postId = report.postId();
        this.reason = report.reason();
        this.details = report.details();
        this.status = report.status();
        this.createdAt = report.createdAt();
    }

    static ReportEntity from(Report report) {
        return new ReportEntity(report);
    }

    Report toDomain() {
        return new Report(id, reporterId, reportedUserId, postId, reason, details, status, createdAt);
    }
}

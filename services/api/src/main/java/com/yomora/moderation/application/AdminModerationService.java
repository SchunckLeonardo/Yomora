package com.yomora.moderation.application;

import com.yomora.moderation.domain.ModerationRepository;
import com.yomora.moderation.domain.Report;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class AdminModerationService {
    private static final Set<String> REPORT_STATUSES = Set.of("OPEN", "REVIEWED", "DISMISSED", "ACTIONED");

    private final ModerationRepository repository;
    private final AdminAccessPolicy adminAccess;

    public AdminModerationService(ModerationRepository repository, AdminAccessPolicy adminAccess) {
        this.repository = repository;
        this.adminAccess = adminAccess;
    }

    public void requireAdmin(UUID administratorId) {
        adminAccess.requireAdmin(administratorId);
    }

    @Transactional(readOnly = true)
    public List<Report> reports(UUID administratorId, String status) {
        adminAccess.requireAdmin(administratorId);
        return repository.findReportsByStatus(normalizeStatus(status));
    }

    @Transactional
    public Report updateReportStatus(UUID administratorId, UUID reportId, String status) {
        adminAccess.requireAdmin(administratorId);
        String normalized = normalizeStatus(status);
        Report current = repository.findReportById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Denúncia não encontrada"));
        return repository.saveReport(new Report(
                current.id(), current.reporterId(), current.reportedUserId(), current.postId(),
                current.reason(), current.details(), normalized, current.createdAt()
        ));
    }

    private String normalizeStatus(String status) {
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        if (!REPORT_STATUSES.contains(normalized)) {
            throw new IllegalArgumentException("Status de denúncia inválido");
        }
        return normalized;
    }
}

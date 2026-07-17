package com.yomora.moderation.application;

import com.yomora.moderation.domain.BlockedUser;
import com.yomora.moderation.domain.ModerationRepository;
import com.yomora.moderation.domain.Report;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdminModerationServiceTest {
    private final UUID adminId = UUID.randomUUID();
    private final InMemoryModerationRepository repository = new InMemoryModerationRepository();
    private final AdminModerationService service = new AdminModerationService(
            repository, new AdminAccessPolicy(Set.of(adminId))
    );

    @Test
    void listsReportsByStatusAndRecordsTheAdministrativeDecision() {
        Report report = repository.saveReport(report("OPEN"));
        repository.saveReport(report("DISMISSED"));

        assertThat(service.reports(adminId, "OPEN")).containsExactly(report);

        Report reviewed = service.updateReportStatus(adminId, report.id(), "ACTIONED");

        assertThat(reviewed.status()).isEqualTo("ACTIONED");
        assertThat(repository.findReportById(report.id())).contains(reviewed);
    }

    @Test
    void rejectsUnknownStatusesAndNonAdministrators() {
        Report report = repository.saveReport(report("OPEN"));

        assertThatThrownBy(() -> service.updateReportStatus(adminId, report.id(), "IGNORED"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.reports(UUID.randomUUID(), "OPEN"))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }

    private Report report(String status) {
        return new Report(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null,
                "SPAM", null, status, Instant.parse("2026-07-17T12:00:00Z"));
    }

    private static final class InMemoryModerationRepository implements ModerationRepository {
        private final List<Report> reports = new ArrayList<>();
        @Override public Optional<BlockedUser> findBlock(UUID blockerId, UUID blockedId) { return Optional.empty(); }
        @Override public BlockedUser saveBlock(BlockedUser value) { return value; }
        @Override public void deleteBlock(BlockedUser value) { }
        @Override public Report saveReport(Report report) {
            reports.removeIf(value -> value.id().equals(report.id()));
            reports.add(report);
            return report;
        }
        @Override public Optional<Report> findReportById(UUID reportId) {
            return reports.stream().filter(value -> value.id().equals(reportId)).findFirst();
        }
        @Override public List<Report> findReportsByStatus(String status) {
            return reports.stream().filter(value -> value.status().equals(status)).toList();
        }
    }
}

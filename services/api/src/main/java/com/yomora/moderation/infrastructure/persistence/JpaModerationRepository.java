package com.yomora.moderation.infrastructure.persistence;

import com.yomora.moderation.domain.BlockedUser;
import com.yomora.moderation.domain.ModerationRepository;
import com.yomora.moderation.domain.Report;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaModerationRepository implements ModerationRepository {
    private final SpringDataBlockedUserRepository blockedRepository;
    private final SpringDataReportRepository reportRepository;

    JpaModerationRepository(
            SpringDataBlockedUserRepository blockedRepository,
            SpringDataReportRepository reportRepository
    ) {
        this.blockedRepository = blockedRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    public Optional<BlockedUser> findBlock(UUID blockerId, UUID blockedId) {
        return blockedRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)
                .map(BlockedUserEntity::toDomain);
    }

    @Override
    public BlockedUser saveBlock(BlockedUser value) {
        return blockedRepository.save(BlockedUserEntity.from(value)).toDomain();
    }

    @Override
    public void deleteBlock(BlockedUser value) {
        blockedRepository.deleteById(value.id());
    }

    @Override
    public Report saveReport(Report report) {
        return reportRepository.save(ReportEntity.from(report)).toDomain();
    }

    @Override
    public Optional<Report> findReportById(UUID reportId) {
        return reportRepository.findById(reportId).map(ReportEntity::toDomain);
    }

    @Override
    public List<Report> findReportsByStatus(String status) {
        return reportRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(ReportEntity::toDomain)
                .toList();
    }
}

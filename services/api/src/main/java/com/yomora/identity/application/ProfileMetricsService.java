package com.yomora.identity.application;

import com.yomora.reading.application.StatisticsService;
import com.yomora.reading.application.StatisticsSummary;
import com.yomora.reading.domain.ReadingSessionRepository;
import com.yomora.social.domain.SocialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProfileMetricsService {
    private final SocialRepository socialRepository;
    private final StatisticsService statisticsService;
    private final ReadingSessionRepository sessionRepository;

    public ProfileMetricsService(
            SocialRepository socialRepository,
            StatisticsService statisticsService,
            ReadingSessionRepository sessionRepository
    ) {
        this.socialRepository = socialRepository;
        this.statisticsService = statisticsService;
        this.sessionRepository = sessionRepository;
    }

    @Transactional(readOnly = true)
    public ProfileMetrics forUser(UUID userId) {
        StatisticsSummary statistics = statisticsService.summary(userId);
        long totalMinutes = sessionRepository.list(userId).stream()
                .filter(session -> session.durationSeconds() != null)
                .mapToLong(session -> session.durationSeconds() / 60)
                .sum();
        return new ProfileMetrics(
                socialRepository.followers(userId).size(),
                socialRepository.following(userId).size(),
                statistics.finishedBooksThisYear(),
                totalMinutes,
                statistics.currentStreak()
        );
    }
}

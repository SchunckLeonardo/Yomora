package com.yomora.identity.application;

import com.yomora.reading.application.StatisticsService;
import com.yomora.reading.application.StatisticsSummary;
import com.yomora.reading.domain.ReadingSession;
import com.yomora.reading.domain.ReadingSessionRepository;
import com.yomora.social.domain.SocialRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProfileMetricsServiceTest {
    @Test
    void combinesSocialAndReadingMetricsForAProfile() {
        UUID userId = UUID.randomUUID();
        SocialRepository social = mock(SocialRepository.class);
        StatisticsService statistics = mock(StatisticsService.class);
        ReadingSessionRepository sessions = mock(ReadingSessionRepository.class);
        when(social.followers(userId)).thenReturn(List.of(UUID.randomUUID(), UUID.randomUUID()));
        when(social.following(userId)).thenReturn(List.of(UUID.randomUUID()));
        when(statistics.summary(userId)).thenReturn(new StatisticsSummary(
                20, 80, 50, 12, 6, 10, 4, Map.of(), 2, 70
        ));
        when(sessions.list(userId)).thenReturn(List.of(
                session(userId, 1_800L), session(userId, 3_600L)
        ));

        ProfileMetrics metrics = new ProfileMetricsService(social, statistics, sessions).forUser(userId);

        assertThat(metrics.followers()).isEqualTo(2);
        assertThat(metrics.following()).isEqualTo(1);
        assertThat(metrics.finishedBooks()).isEqualTo(4);
        assertThat(metrics.totalReadingMinutes()).isEqualTo(90);
        assertThat(metrics.currentStreak()).isEqualTo(6);
    }

    private ReadingSession session(UUID userId, Long seconds) {
        Instant now = Instant.parse("2026-07-13T12:00:00Z");
        return new ReadingSession(UUID.randomUUID(), userId, UUID.randomUUID(), 1, 10, 10, null,
                now.minusSeconds(seconds), null, 0, now, seconds, 9, null);
    }
}

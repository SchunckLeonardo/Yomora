package com.yomora.reading.application;

import com.yomora.reading.domain.ReadingGoal;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReadingConsistencyCalculatorTest {
    @Test
    void calculatesCurrentBestStreakAndThirtyDayPaceWithoutPunitiveRounding() {
        LocalDate today = LocalDate.of(2026, 7, 13);
        Map<LocalDate, DailyReading> readings = new LinkedHashMap<>();
        readings.put(today.minusDays(2), new DailyReading(30, 20));
        readings.put(today.minusDays(1), new DailyReading(20, 10));
        readings.put(today, new DailyReading(25, 12));
        readings.put(today.minusDays(12), new DailyReading(30, 10));
        readings.put(today.minusDays(13), new DailyReading(30, 10));
        readings.put(today.minusDays(14), new DailyReading(30, 10));
        readings.put(today.minusDays(15), new DailyReading(30, 10));

        ConsistencySummary summary = new ReadingConsistencyCalculator().calculate(
                new ReadingGoal(20, 5, null), readings, today
        );

        assertThat(summary.currentStreak()).isEqualTo(3);
        assertThat(summary.bestStreak()).isEqualTo(4);
        assertThat(summary.daysReadLast30()).isEqualTo(7);
        assertThat(summary.pacePercent()).isEqualTo(31.82);
    }
}

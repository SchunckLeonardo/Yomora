package com.yomora.reading.application;

import com.yomora.catalog.domain.BookCatalogRepository;
import com.yomora.library.domain.ReadingStatus;
import com.yomora.library.domain.UserBookRepository;
import com.yomora.reading.domain.ReadingGoal;
import com.yomora.reading.domain.ReadingGoalRepository;
import com.yomora.reading.domain.ReadingSessionRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StatisticsService {
    private final ReadingSessionRepository sessionRepository;
    private final ReadingGoalRepository goalRepository;
    private final UserBookRepository userBookRepository;
    private final BookCatalogRepository catalogRepository;
    private final ReadingConsistencyCalculator calculator;
    private final Clock clock;

    public StatisticsService(
            ReadingSessionRepository sessionRepository,
            ReadingGoalRepository goalRepository,
            UserBookRepository userBookRepository,
            BookCatalogRepository catalogRepository,
            ReadingConsistencyCalculator calculator,
            Clock clock
    ) {
        this.sessionRepository = sessionRepository;
        this.goalRepository = goalRepository;
        this.userBookRepository = userBookRepository;
        this.catalogRepository = catalogRepository;
        this.calculator = calculator;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public StatisticsSummary summary(UUID userId) {
        LocalDate today = LocalDate.ofInstant(clock.instant(), ZoneOffset.UTC);
        Map<LocalDate, DailyReading> daily = sessionRepository.dailyReading(userId, today.minusDays(29));
        ReadingGoal goal = goalRepository.findByUserId(userId).orElse(new ReadingGoal(20, 5, null));
        ConsistencySummary consistency = calculator.calculate(goal, daily, today);
        LocalDate weekStart = today.minusDays(6);
        long weekMinutes = daily.entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(weekStart))
                .mapToLong(entry -> entry.getValue().minutes())
                .sum();
        long weekPages = daily.entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(weekStart))
                .mapToLong(entry -> entry.getValue().pages())
                .sum();
        var finishedBooks = userBookRepository.list(userId, ReadingStatus.FINISHED);
        long finishedThisYear = finishedBooks.stream()
                .filter(book -> book.finishedAt() != null && book.finishedAt().atZone(ZoneOffset.UTC).getYear() == today.getYear())
                .count();

        Map<String, Long> genres = finishedBooks.stream()
                .flatMap(book -> catalogRepository.findEdition(book.editionId()).stream())
                .flatMap(edition -> edition.categories().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (left, right) -> left, LinkedHashMap::new
                ));

        return new StatisticsSummary(
                daily.getOrDefault(today, new DailyReading(0, 0)).minutes(),
                weekMinutes,
                weekPages,
                consistency.daysReadLast30(),
                consistency.currentStreak(),
                consistency.bestStreak(),
                finishedThisYear,
                genres,
                sessionRepository.list(userId).stream().filter(session -> !session.active()).count(),
                consistency.pacePercent()
        );
    }
}

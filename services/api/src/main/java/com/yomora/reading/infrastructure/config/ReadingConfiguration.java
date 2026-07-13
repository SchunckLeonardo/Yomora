package com.yomora.reading.infrastructure.config;

import com.yomora.catalog.domain.BookCatalogRepository;
import com.yomora.library.domain.UserBookRepository;
import com.yomora.reading.application.ReadingConsistencyCalculator;
import com.yomora.reading.application.ReadingGoalService;
import com.yomora.reading.application.ReadingSessionService;
import com.yomora.reading.application.StatisticsService;
import com.yomora.reading.domain.ReadingGoalRepository;
import com.yomora.reading.domain.ReadingSessionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
class ReadingConfiguration {
    @Bean
    ReadingConsistencyCalculator readingConsistencyCalculator() {
        return new ReadingConsistencyCalculator();
    }

    @Bean
    ReadingGoalService readingGoalService(ReadingGoalRepository repository) {
        return new ReadingGoalService(repository);
    }

    @Bean
    ReadingSessionService readingSessionService(
            ReadingSessionRepository sessionRepository,
            UserBookRepository userBookRepository,
            BookCatalogRepository catalogRepository,
            Clock clock,
            ReadingGoalRepository goalRepository,
            ReadingConsistencyCalculator calculator
    ) {
        return new ReadingSessionService(
                sessionRepository, userBookRepository, catalogRepository, clock, goalRepository, calculator
        );
    }

    @Bean
    StatisticsService statisticsService(
            ReadingSessionRepository sessionRepository,
            ReadingGoalRepository goalRepository,
            UserBookRepository userBookRepository,
            BookCatalogRepository catalogRepository,
            ReadingConsistencyCalculator calculator,
            Clock clock
    ) {
        return new StatisticsService(
                sessionRepository, goalRepository, userBookRepository, catalogRepository, calculator, clock
        );
    }
}

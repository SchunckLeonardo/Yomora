package com.yomora.reading.web;

import com.yomora.reading.application.StatisticsService;
import com.yomora.reading.application.StatisticsSummary;
import com.yomora.shared.security.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistics")
class StatisticsController {
    private final StatisticsService service;
    private final CurrentUser currentUser;

    StatisticsController(StatisticsService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @GetMapping("/summary")
    StatisticsSummary summary(Authentication authentication) {
        return service.summary(currentUser.id(authentication));
    }
}

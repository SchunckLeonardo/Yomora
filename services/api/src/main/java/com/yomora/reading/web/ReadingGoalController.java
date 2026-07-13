package com.yomora.reading.web;

import com.yomora.reading.application.ReadingGoalService;
import com.yomora.reading.domain.ReadingGoal;
import com.yomora.shared.security.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reading-goals")
class ReadingGoalController {
    private final ReadingGoalService service;
    private final CurrentUser currentUser;

    ReadingGoalController(ReadingGoalService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @GetMapping
    ReadingGoal get(Authentication authentication) {
        return service.get(currentUser.id(authentication));
    }

    @PutMapping
    ReadingGoal update(Authentication authentication, @Valid @RequestBody GoalRequest request) {
        return service.update(
                currentUser.id(authentication),
                new ReadingGoal(request.dailyMinutes(), request.weeklyDays(), request.dailyPages())
        );
    }

    record GoalRequest(
            @Min(1) @Max(1440) int dailyMinutes,
            @Min(1) @Max(7) int weeklyDays,
            @Min(1) Integer dailyPages
    ) {
    }
}

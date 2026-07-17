package com.yomora.moderation.web;

import com.yomora.moderation.application.AdminModerationService;
import com.yomora.moderation.application.CommunityAccess;
import com.yomora.moderation.application.CommunitySuspensionService;
import com.yomora.moderation.domain.CommunitySuspension;
import com.yomora.moderation.domain.Report;
import com.yomora.shared.security.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/moderation")
class AdminModerationController {
    private final AdminModerationService moderation;
    private final CommunitySuspensionService suspensions;
    private final CurrentUser currentUser;

    AdminModerationController(
            AdminModerationService moderation,
            CommunitySuspensionService suspensions,
            CurrentUser currentUser
    ) {
        this.moderation = moderation;
        this.suspensions = suspensions;
        this.currentUser = currentUser;
    }

    @GetMapping("/reports")
    List<Report> reports(
            Authentication authentication,
            @RequestParam(defaultValue = "OPEN") String status
    ) {
        return moderation.reports(currentUser.id(authentication), status);
    }

    @PatchMapping("/reports/{reportId}")
    Report updateReport(
            Authentication authentication,
            @PathVariable UUID reportId,
            @Valid @RequestBody ReportDecisionRequest request
    ) {
        return moderation.updateReportStatus(currentUser.id(authentication), reportId, request.status());
    }

    @PostMapping("/suspensions")
    @ResponseStatus(HttpStatus.CREATED)
    CommunitySuspension suspend(
            Authentication authentication,
            @Valid @RequestBody SuspensionRequest request
    ) {
        return suspensions.suspend(
                currentUser.id(authentication), request.userId(), request.days(), request.reason()
        );
    }

    @DeleteMapping("/suspensions/{suspensionId}")
    CommunitySuspension reverse(Authentication authentication, @PathVariable UUID suspensionId) {
        return suspensions.reverse(currentUser.id(authentication), suspensionId);
    }

    @GetMapping("/users/{userId}/community-access")
    CommunityAccess access(Authentication authentication, @PathVariable UUID userId) {
        moderation.requireAdmin(currentUser.id(authentication));
        return suspensions.accessFor(userId);
    }

    record ReportDecisionRequest(@NotBlank String status) {
    }

    record SuspensionRequest(
            @NotNull UUID userId,
            @Min(1) @Max(30) int days,
            @NotBlank @Size(max = 500) String reason
    ) {
    }
}

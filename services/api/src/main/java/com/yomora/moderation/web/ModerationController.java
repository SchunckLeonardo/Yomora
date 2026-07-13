package com.yomora.moderation.web;

import com.yomora.moderation.application.ModerationService;
import com.yomora.moderation.domain.BlockedUser;
import com.yomora.moderation.domain.Report;
import com.yomora.shared.security.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/moderation")
class ModerationController {
    private final ModerationService service;
    private final CurrentUser currentUser;

    ModerationController(ModerationService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @PostMapping("/blocks/{userId}")
    ResponseEntity<BlockedUser> block(Authentication authentication, @PathVariable UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.block(currentUser.id(authentication), userId));
    }

    @DeleteMapping("/blocks/{userId}")
    ResponseEntity<Void> unblock(Authentication authentication, @PathVariable UUID userId) {
        service.unblock(currentUser.id(authentication), userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reports")
    ResponseEntity<Report> report(Authentication authentication, @Valid @RequestBody ReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.report(
                currentUser.id(authentication), request.reportedUserId(), request.postId(),
                request.reason(), request.details()
        ));
    }

    record ReportRequest(
            UUID reportedUserId,
            UUID postId,
            @NotBlank @Size(max = 100) String reason,
            @Size(max = 2000) String details
    ) {
    }
}

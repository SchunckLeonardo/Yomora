package com.yomora.reading.web;

import com.yomora.reading.application.ReadingSessionService;
import com.yomora.reading.application.ReadingSessionSummary;
import com.yomora.reading.domain.ReadingSession;
import com.yomora.shared.security.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reading-sessions")
class ReadingSessionController {
    private final ReadingSessionService service;
    private final CurrentUser currentUser;

    ReadingSessionController(ReadingSessionService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @PostMapping
    ResponseEntity<ReadingSession> start(Authentication authentication, @Valid @RequestBody StartRequest request) {
        ReadingSession session = service.start(
                currentUser.id(authentication), request.userBookId(), request.startPage(), request.goalPages()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @PatchMapping("/{id}/finish")
    ReadingSessionSummary finish(
            Authentication authentication,
            @PathVariable UUID id,
            @Valid @RequestBody FinishRequest request
    ) {
        return service.finish(currentUser.id(authentication), id, request.endPage(), request.note());
    }

    @PatchMapping("/{id}/pause")
    ReadingSession pause(Authentication authentication, @PathVariable UUID id) {
        return service.pause(currentUser.id(authentication), id);
    }

    @PatchMapping("/{id}/resume")
    ReadingSession resume(Authentication authentication, @PathVariable UUID id) {
        return service.resume(currentUser.id(authentication), id);
    }

    @PatchMapping("/{id}/progress")
    ReadingSession progress(
            Authentication authentication,
            @PathVariable UUID id,
            @Valid @RequestBody ProgressRequest request
    ) {
        return service.updateProgress(currentUser.id(authentication), id, request.currentPage());
    }

    @GetMapping
    List<ReadingSession> list(Authentication authentication) {
        return service.list(currentUser.id(authentication));
    }

    @GetMapping("/active")
    ResponseEntity<ReadingSession> active(Authentication authentication) {
        return service.active(currentUser.id(authentication))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    record StartRequest(
            @NotNull UUID userBookId,
            @Min(0) Integer startPage,
            @Min(1) Integer goalPages
    ) {
    }

    record FinishRequest(@Min(0) int endPage, @Size(max = 2000) String note) {
    }

    record ProgressRequest(@Min(0) int currentPage) {
    }
}

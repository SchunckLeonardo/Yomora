package com.yomora.social.web;

import com.yomora.shared.security.CurrentUser;
import com.yomora.social.application.CommunityActivityService;
import com.yomora.social.domain.CommunityActivity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/community/activities")
class CommunityActivityController {
    private final CommunityActivityService service;
    private final CurrentUser currentUser;

    CommunityActivityController(CommunityActivityService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @GetMapping
    List<CommunityActivity> list(Authentication authentication) {
        return service.list(currentUser.id(authentication));
    }

    @PatchMapping("/{id}/read")
    CommunityActivity markRead(Authentication authentication, @PathVariable UUID id) {
        return service.markRead(currentUser.id(authentication), id);
    }
}

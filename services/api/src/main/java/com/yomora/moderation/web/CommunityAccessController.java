package com.yomora.moderation.web;

import com.yomora.moderation.application.CommunityAccess;
import com.yomora.moderation.application.CommunitySuspensionService;
import com.yomora.shared.security.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/community/access")
class CommunityAccessController {
    private final CommunitySuspensionService suspensions;
    private final CurrentUser currentUser;

    CommunityAccessController(CommunitySuspensionService suspensions, CurrentUser currentUser) {
        this.suspensions = suspensions;
        this.currentUser = currentUser;
    }

    @GetMapping
    CommunityAccess access(Authentication authentication) {
        return suspensions.accessFor(currentUser.id(authentication));
    }
}

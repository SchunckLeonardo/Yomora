package com.yomora.social.web;

import com.yomora.shared.security.CurrentUser;
import com.yomora.social.application.SocialService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
class FollowController {
    private final SocialService service;
    private final CurrentUser currentUser;

    FollowController(SocialService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @PostMapping("/{id}/follow")
    ResponseEntity<Void> follow(Authentication authentication, @PathVariable UUID id) {
        service.follow(currentUser.id(authentication), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/follow")
    ResponseEntity<Void> unfollow(Authentication authentication, @PathVariable UUID id) {
        service.unfollow(currentUser.id(authentication), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/followers")
    List<UUID> followers(@PathVariable UUID id) {
        return service.followers(id);
    }

    @GetMapping("/{id}/following")
    List<UUID> following(@PathVariable UUID id) {
        return service.following(id);
    }
}

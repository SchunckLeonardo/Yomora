package com.yomora.social.web;

import com.yomora.shared.security.CurrentUser;
import com.yomora.social.application.SocialService;
import com.yomora.social.domain.FollowRequest;
import com.yomora.social.domain.FollowStatus;
import org.springframework.http.HttpStatus;
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
    ResponseEntity<FollowStatusResponse> follow(Authentication authentication, @PathVariable UUID id) {
        FollowStatus status = service.follow(currentUser.id(authentication), id);
        HttpStatus responseStatus = status == FollowStatus.PENDING ? HttpStatus.ACCEPTED : HttpStatus.OK;
        return ResponseEntity.status(responseStatus).body(new FollowStatusResponse(status));
    }

    @DeleteMapping("/{id}/follow")
    ResponseEntity<Void> unfollow(Authentication authentication, @PathVariable UUID id) {
        service.unfollow(currentUser.id(authentication), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/followers")
    List<UUID> followers(Authentication authentication, @PathVariable UUID id) {
        return service.followers(currentUser.id(authentication), id);
    }

    @GetMapping("/{id}/following")
    List<UUID> following(Authentication authentication, @PathVariable UUID id) {
        return service.following(currentUser.id(authentication), id);
    }

    @GetMapping("/{id}/follow-status")
    FollowStatusResponse followStatus(Authentication authentication, @PathVariable UUID id) {
        return new FollowStatusResponse(service.followStatus(currentUser.id(authentication), id).orElse(null));
    }

    @GetMapping("/me/follow-requests")
    List<FollowRequest> pendingRequests(Authentication authentication) {
        return service.pendingFollowRequests(currentUser.id(authentication));
    }

    @PostMapping("/me/follow-requests/{followerId}/approve")
    ResponseEntity<Void> approve(Authentication authentication, @PathVariable UUID followerId) {
        service.approveFollow(currentUser.id(authentication), followerId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/follow-requests/{followerId}")
    ResponseEntity<Void> reject(Authentication authentication, @PathVariable UUID followerId) {
        service.rejectFollow(currentUser.id(authentication), followerId);
        return ResponseEntity.noContent().build();
    }

    record FollowStatusResponse(FollowStatus status) {
    }
}

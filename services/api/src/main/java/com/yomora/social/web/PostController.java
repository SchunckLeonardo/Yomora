package com.yomora.social.web;

import com.yomora.shared.security.CurrentUser;
import com.yomora.social.application.SocialService;
import com.yomora.social.domain.Comment;
import com.yomora.social.domain.Post;
import com.yomora.social.domain.PostType;
import com.yomora.social.domain.Visibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
class PostController {
    private final SocialService service;
    private final CurrentUser currentUser;

    PostController(SocialService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @GetMapping("/feed")
    List<Post> feed(
            Authentication authentication,
            @RequestParam(required = false) Instant cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int limit
    ) {
        return service.followingFeed(currentUser.id(authentication), cursor, limit);
    }

    @GetMapping("/discover")
    List<Post> discover(
            @RequestParam(required = false) Instant cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int limit
    ) {
        return service.discover(cursor, limit);
    }

    @GetMapping("/{id}")
    Post details(Authentication authentication, @PathVariable UUID id) {
        return service.getPostFor(currentUser.id(authentication), id);
    }

    @PostMapping
    ResponseEntity<Post> create(Authentication authentication, @Valid @RequestBody CreatePostRequest request) {
        Post post = service.createPost(
                currentUser.id(authentication), request.text(), request.editionId(), request.type(),
                request.spoiler(), request.spoilerPage(), request.visibility()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @PatchMapping("/{id}")
    Post edit(
            Authentication authentication,
            @PathVariable UUID id,
            @Valid @RequestBody EditPostRequest request
    ) {
        return service.editPost(
                currentUser.id(authentication), id, request.text(), request.spoiler(),
                request.spoilerPage(), request.visibility()
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(Authentication authentication, @PathVariable UUID id) {
        service.deletePost(currentUser.id(authentication), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/likes")
    Post like(Authentication authentication, @PathVariable UUID id) {
        return service.like(currentUser.id(authentication), id);
    }

    @DeleteMapping("/{id}/likes")
    Post unlike(Authentication authentication, @PathVariable UUID id) {
        return service.unlike(currentUser.id(authentication), id);
    }

    @GetMapping("/{id}/comments")
    List<Comment> comments(@PathVariable UUID id) {
        return service.comments(id);
    }

    @PostMapping("/{id}/comments")
    ResponseEntity<Comment> comment(
            Authentication authentication,
            @PathVariable UUID id,
            @Valid @RequestBody CommentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.comment(currentUser.id(authentication), id, request.text()));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    ResponseEntity<Void> deleteComment(
            Authentication authentication,
            @PathVariable UUID postId,
            @PathVariable UUID commentId
    ) {
        service.getPost(postId);
        service.deleteComment(currentUser.id(authentication), commentId);
        return ResponseEntity.noContent().build();
    }

    record CreatePostRequest(
            @NotBlank @Size(max = 5000) String text,
            UUID editionId,
            @NotNull PostType type,
            boolean spoiler,
            @Min(0) Integer spoilerPage,
            @NotNull Visibility visibility
    ) {
    }

    record EditPostRequest(
            @NotBlank @Size(max = 5000) String text,
            boolean spoiler,
            @Min(0) Integer spoilerPage,
            @NotNull Visibility visibility
    ) {
    }

    record CommentRequest(@NotBlank @Size(max = 2000) String text) {
    }
}

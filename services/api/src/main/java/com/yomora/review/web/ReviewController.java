package com.yomora.review.web;

import com.yomora.review.application.ReviewService;
import com.yomora.review.domain.Review;
import com.yomora.shared.security.CurrentUser;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
class ReviewController {
    private final ReviewService service;
    private final CurrentUser currentUser;

    ReviewController(ReviewService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @PostMapping
    ResponseEntity<Review> save(Authentication authentication, @Valid @RequestBody ReviewRequest request) {
        Review review = service.save(
                currentUser.id(authentication), request.workId(), request.editionId(), request.rating(),
                request.title(), request.text(), request.spoiler()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @PatchMapping("/{id}")
    Review update(
            Authentication authentication,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReviewRequest request
    ) {
        return service.update(
                currentUser.id(authentication), id, request.rating(), request.title(), request.text(), request.spoiler()
        );
    }

    @GetMapping
    List<Review> list(@RequestParam UUID workId) {
        return service.listForWork(workId);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(Authentication authentication, @PathVariable UUID id) {
        service.delete(currentUser.id(authentication), id);
        return ResponseEntity.noContent().build();
    }

    record ReviewRequest(
            @NotNull UUID workId,
            UUID editionId,
            @Min(1) @Max(5) int rating,
            @Size(max = 200) String title,
            @NotBlank @Size(max = 5000) String text,
            boolean spoiler
    ) {
    }

    record UpdateReviewRequest(
            @Min(1) @Max(5) int rating,
            @Size(max = 200) String title,
            @NotBlank @Size(max = 5000) String text,
            boolean spoiler
    ) {
    }
}

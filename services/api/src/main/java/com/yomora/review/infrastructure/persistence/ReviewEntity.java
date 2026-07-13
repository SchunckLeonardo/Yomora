package com.yomora.review.infrastructure.persistence;

import com.yomora.review.domain.Review;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reviews")
class ReviewEntity {
    @Id
    UUID id;
    @Column(name = "user_id")
    UUID userId;
    @Column(name = "work_id")
    UUID workId;
    @Column(name = "edition_id")
    UUID editionId;
    int rating;
    String title;
    String text;
    boolean spoiler;
    @Column(name = "post_id")
    UUID postId;
    @Column(name = "created_at")
    Instant createdAt;
    @Column(name = "updated_at")
    Instant updatedAt;

    protected ReviewEntity() {
    }

    private ReviewEntity(Review review) {
        this.id = review.id();
        this.userId = review.userId();
        this.workId = review.workId();
        this.editionId = review.editionId();
        this.rating = review.rating();
        this.title = review.title();
        this.text = review.text();
        this.spoiler = review.spoiler();
        this.postId = review.postId();
        this.createdAt = review.createdAt();
        this.updatedAt = review.updatedAt();
    }

    static ReviewEntity from(Review review) {
        return new ReviewEntity(review);
    }

    Review toDomain() {
        return new Review(
                id, userId, workId, editionId, rating, title, text, spoiler, postId, createdAt, updatedAt
        );
    }
}

package com.yomora.review.application;

import com.yomora.review.domain.Review;
import com.yomora.review.domain.ReviewRepository;
import com.yomora.social.domain.PostType;
import com.yomora.social.domain.Visibility;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ReviewService {
    private final ReviewRepository repository;
    private final SocialPublisher publisher;
    private final Clock clock;

    public ReviewService(ReviewRepository repository, SocialPublisher publisher, Clock clock) {
        this.repository = repository;
        this.publisher = publisher;
        this.clock = clock;
    }

    @Transactional
    public Review save(
            UUID userId,
            UUID workId,
            UUID editionId,
            int rating,
            String title,
            String text,
            boolean spoiler
    ) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("A avaliação deve estar entre 1 e 5");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("O texto da review é obrigatório");
        }
        Instant now = clock.instant();
        Review current = repository.findByUserAndWork(userId, workId).orElse(null);
        UUID postId = publisher.upsert(
                userId,
                current == null ? null : current.postId(),
                editionId,
                composePost(title, text),
                PostType.REVIEW,
                spoiler,
                Visibility.PUBLIC
        );
        return repository.save(new Review(
                current == null ? UUID.randomUUID() : current.id(),
                userId,
                workId,
                editionId,
                rating,
                title == null || title.isBlank() ? null : title.trim(),
                text.trim(),
                spoiler,
                postId,
                current == null ? now : current.createdAt(),
                now
        ));
    }

    @Transactional(readOnly = true)
    public List<Review> listForWork(UUID workId) {
        return repository.listForWork(workId);
    }

    @Transactional
    public Review update(
            UUID userId,
            UUID reviewId,
            int rating,
            String title,
            String text,
            boolean spoiler
    ) {
        Review current = repository.findOwned(reviewId, userId).orElseThrow(ReviewNotFoundException::new);
        if (rating < 1 || rating > 5 || text == null || text.isBlank()) {
            throw new IllegalArgumentException("Review inválida");
        }
        UUID postId = publisher.upsert(
                userId, current.postId(), current.editionId(), composePost(title, text),
                PostType.REVIEW, spoiler, Visibility.PUBLIC
        );
        return repository.save(new Review(
                current.id(), current.userId(), current.workId(), current.editionId(), rating,
                title == null || title.isBlank() ? null : title.trim(), text.trim(), spoiler,
                postId, current.createdAt(), clock.instant()
        ));
    }

    @Transactional
    public void delete(UUID userId, UUID reviewId) {
        Review review = repository.findOwned(reviewId, userId).orElseThrow(ReviewNotFoundException::new);
        if (review.postId() != null) {
            publisher.delete(userId, review.postId());
        }
        repository.delete(review);
    }

    private String composePost(String title, String text) {
        return title == null || title.isBlank() ? text.trim() : title.trim() + "\n\n" + text.trim();
    }
}

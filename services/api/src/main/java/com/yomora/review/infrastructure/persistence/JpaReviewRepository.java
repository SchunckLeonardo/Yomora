package com.yomora.review.infrastructure.persistence;

import com.yomora.review.domain.Review;
import com.yomora.review.domain.ReviewRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaReviewRepository implements ReviewRepository {
    private final SpringDataReviewRepository repository;

    JpaReviewRepository(SpringDataReviewRepository repository) {
        this.repository = repository;
    }

    @Override
    public Review save(Review review) {
        return repository.save(ReviewEntity.from(review)).toDomain();
    }

    @Override
    public Optional<Review> findByUserAndWork(UUID userId, UUID workId) {
        return repository.findByUserIdAndWorkId(userId, workId).map(ReviewEntity::toDomain);
    }

    @Override
    public Optional<Review> findOwned(UUID id, UUID userId) {
        return repository.findByIdAndUserId(id, userId).map(ReviewEntity::toDomain);
    }

    @Override
    public List<Review> listForWork(UUID workId) {
        return repository.findAllByWorkIdOrderByCreatedAtDesc(workId).stream().map(ReviewEntity::toDomain).toList();
    }

    @Override
    public void delete(Review review) {
        repository.deleteById(review.id());
    }
}

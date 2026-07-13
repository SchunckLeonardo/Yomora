package com.yomora.review.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository {
    Review save(Review review);

    Optional<Review> findByUserAndWork(UUID userId, UUID workId);

    Optional<Review> findOwned(UUID id, UUID userId);

    List<Review> listForWork(UUID workId);

    void delete(Review review);
}

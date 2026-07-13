package com.yomora.review.application;

import com.yomora.review.domain.Note;
import com.yomora.review.domain.NoteRepository;
import com.yomora.review.domain.Review;
import com.yomora.review.domain.ReviewRepository;
import com.yomora.social.domain.PostType;
import com.yomora.social.domain.Visibility;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewAndNoteServiceTest {
    @Test
    void upsertsTheSingleMainReviewPerUserAndWork() {
        UUID userId = UUID.randomUUID();
        UUID workId = UUID.randomUUID();
        UUID editionId = UUID.randomUUID();
        InMemoryReviewRepository repository = new InMemoryReviewRepository();
        CapturingPublisher publisher = new CapturingPublisher();
        ReviewService service = new ReviewService(repository, publisher, fixedClock());

        Review first = service.save(userId, workId, editionId, 4, "Ótimo", "Gostei muito", false);
        Review updated = service.save(userId, workId, editionId, 5, "Excelente", "Virou favorito", false);

        assertThat(updated.id()).isEqualTo(first.id());
        assertThat(updated.rating()).isEqualTo(5);
        assertThat(updated.postId()).isEqualTo(first.postId());
        assertThat(repository.reviews).hasSize(1);
    }

    @Test
    void privateNotesNeverCreateFeedPosts() {
        InMemoryNoteRepository repository = new InMemoryNoteRepository();
        CapturingPublisher publisher = new CapturingPublisher();
        NoteService service = new NoteService(repository, publisher, fixedClock());
        UUID userId = UUID.randomUUID();
        UUID editionId = UUID.randomUUID();

        Note privateNote = service.create(userId, editionId, "Minha interpretação", 42, "Capítulo 3", true, false);
        Note publicNote = service.create(userId, editionId, "Uma citação pública", 43, null, false, false);

        assertThat(privateNote.postId()).isNull();
        assertThat(publicNote.postId()).isNotNull();
        assertThat(publisher.posts).hasSize(1);
    }

    private static Clock fixedClock() {
        return Clock.fixed(Instant.parse("2026-07-13T18:00:00Z"), ZoneOffset.UTC);
    }

    private static final class CapturingPublisher implements SocialPublisher {
        private final Map<UUID, String> posts = new HashMap<>();

        @Override
        public UUID upsert(
                UUID authorId,
                UUID existingPostId,
                UUID editionId,
                String text,
                PostType type,
                boolean spoiler,
                Visibility visibility
        ) {
            UUID postId = existingPostId == null ? UUID.randomUUID() : existingPostId;
            posts.put(postId, text);
            return postId;
        }

        @Override
        public void delete(UUID authorId, UUID postId) {
            posts.remove(postId);
        }
    }

    private static final class InMemoryReviewRepository implements ReviewRepository {
        private final Map<UUID, Review> reviews = new HashMap<>();

        @Override
        public Review save(Review review) {
            reviews.put(review.id(), review);
            return review;
        }

        @Override
        public Optional<Review> findByUserAndWork(UUID userId, UUID workId) {
            return reviews.values().stream()
                    .filter(review -> review.userId().equals(userId) && review.workId().equals(workId))
                    .findFirst();
        }

        @Override
        public Optional<Review> findOwned(UUID id, UUID userId) {
            return Optional.ofNullable(reviews.get(id)).filter(review -> review.userId().equals(userId));
        }

        @Override
        public List<Review> listForWork(UUID workId) {
            return reviews.values().stream().filter(review -> review.workId().equals(workId)).toList();
        }

        @Override
        public void delete(Review review) {
            reviews.remove(review.id());
        }
    }

    private static final class InMemoryNoteRepository implements NoteRepository {
        private final Map<UUID, Note> notes = new HashMap<>();

        @Override
        public Note save(Note note) {
            notes.put(note.id(), note);
            return note;
        }

        @Override
        public Optional<Note> findOwned(UUID id, UUID userId) {
            return Optional.ofNullable(notes.get(id)).filter(note -> note.userId().equals(userId));
        }

        @Override
        public List<Note> list(UUID userId, UUID editionId) {
            return notes.values().stream()
                    .filter(note -> note.userId().equals(userId) && note.editionId().equals(editionId))
                    .toList();
        }

        @Override
        public void delete(Note note) {
            notes.remove(note.id());
        }
    }
}

package com.yomora;

import com.yomora.catalog.domain.BookCandidate;
import com.yomora.catalog.domain.BookCatalogRepository;
import com.yomora.social.domain.Post;
import com.yomora.social.domain.PostType;
import com.yomora.social.domain.SocialRepository;
import com.yomora.social.domain.Visibility;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class PersistenceQueryIntegrationTest {
    private static final Instant NOW = Instant.parse("2026-07-13T18:00:00Z");

    @Autowired
    BookCatalogRepository bookCatalogRepository;

    @Autowired
    SocialRepository socialRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    EntityManager entityManager;

    @Test
    void searchesLocalBooksWithoutDuplicatesAndOrdersThemByTitle() {
        saveBook("Biblioteca de Babel", List.of("Jorge Luis Borges", "Colaborador"), "catalog-2");
        saveBook("A Biblioteca da Meia-Noite", List.of("Matt Haig"), "catalog-1");

        var results = bookCatalogRepository.search("biblioteca", "pt", 20);

        assertThat(results)
                .extracting(result -> result.title())
                .containsExactly("A Biblioteca da Meia-Noite", "Biblioteca de Babel");
    }

    @Test
    void truncatesAndDeduplicatesExternalCategoriesBeforePersistence() {
        String categoryPrefix = "c".repeat(120);
        BookCandidate candidate = new BookCandidate(
                "Livro com categoria externa longa",
                "Descrição",
                List.of("Autora Yomora"),
                List.of(categoryPrefix + " primeira", categoryPrefix + " segunda"),
                null,
                null,
                "Editora Yomora",
                LocalDate.of(2026, 1, 1),
                "pt",
                200,
                null,
                "integration-test",
                "long-category"
        );

        var saved = bookCatalogRepository.save(candidate);
        entityManager.flush();

        assertThat(saved.categories()).containsExactly(categoryPrefix);
    }

    @Test
    void normalizesNonPositiveExternalPageCountsBeforePersistence() {
        for (int pageCount : List.of(0, -25)) {
            BookCandidate candidate = new BookCandidate(
                    "Livro com contagem inválida " + pageCount,
                    "Descrição",
                    List.of("Autora Yomora"),
                    List.of("Ficção"),
                    null,
                    null,
                    "Editora Yomora",
                    LocalDate.of(2026, 1, 1),
                    "pt",
                    pageCount,
                    null,
                    "integration-test",
                    "invalid-page-count-" + pageCount
            );

            var saved = bookCatalogRepository.save(candidate);
            entityManager.flush();

            assertThat(saved.pageCount()).isNull();
        }
    }

    @Test
    void loadsFirstFollowingFeedPageWithoutCursor() {
        UUID followerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        insertUser(followerId, "reader");
        insertUser(authorId, "author");
        socialRepository.setFollowing(followerId, authorId, true);
        Post post = savePublicPost(authorId, "Leitura compartilhada");

        var feed = socialRepository.followingFeed(followerId, null, 20);

        assertThat(feed).extracting(Post::id).containsExactly(post.id());
    }

    @Test
    void loadsFirstDiscoverPageWithoutCursor() {
        UUID authorId = UUID.randomUUID();
        insertUser(authorId, "discover-author");
        Post post = savePublicPost(authorId, "Recomendação pública");

        var discovered = socialRepository.discover(null, 20);

        assertThat(discovered).extracting(Post::id).contains(post.id());
    }

    @Test
    void databaseAllowsOnlyOneActiveReadingSessionPerUser() {
        UUID userId = UUID.randomUUID();
        insertUser(userId, "active-reader");
        UUID firstUserBookId = insertUserBook(userId, "Primeira leitura");
        UUID secondUserBookId = insertUserBook(userId, "Segunda leitura");
        insertActiveSession(userId, firstUserBookId);

        assertThatThrownBy(() -> insertActiveSession(userId, secondUserBookId))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private void saveBook(String title, List<String> authors, String externalId) {
        bookCatalogRepository.save(new BookCandidate(
                title,
                "Descrição",
                authors,
                List.of("Ficção"),
                null,
                null,
                "Editora Yomora",
                LocalDate.of(2026, 1, 1),
                "pt",
                200,
                null,
                "integration-test",
                externalId
        ));
    }

    private Post savePublicPost(UUID authorId, String text) {
        Post post = new Post(
                UUID.randomUUID(),
                authorId,
                text,
                null,
                PostType.RECOMMENDATION,
                false,
                null,
                Visibility.PUBLIC,
                NOW,
                NOW,
                0,
                0
        );
        return socialRepository.savePost(post);
    }

    private void insertUser(UUID userId, String username) {
        jdbcTemplate.update("""
                INSERT INTO users (
                    id, name, username, email, password_hash, bio, public_profile, created_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, '', TRUE, ?, ?)
                """,
                userId,
                username,
                username,
                username + "@example.com",
                "integration-test-password-hash",
                Timestamp.from(NOW),
                Timestamp.from(NOW)
        );
    }

    private UUID insertUserBook(UUID userId, String title) {
        UUID workId = UUID.randomUUID();
        UUID editionId = UUID.randomUUID();
        UUID userBookId = UUID.randomUUID();
        jdbcTemplate.update("""
                INSERT INTO book_works (id, title, description, created_at, updated_at)
                VALUES (?, ?, '', ?, ?)
                """, workId, title, Timestamp.from(NOW), Timestamp.from(NOW));
        jdbcTemplate.update("""
                INSERT INTO book_editions (id, work_id, language, page_count, created_at, updated_at)
                VALUES (?, ?, 'pt', 200, ?, ?)
                """, editionId, workId, Timestamp.from(NOW), Timestamp.from(NOW));
        jdbcTemplate.update("""
                INSERT INTO user_books (
                    id, user_id, edition_id, status, current_page, started_at, created_at, updated_at
                ) VALUES (?, ?, ?, 'READING', 0, ?, ?, ?)
                """, userBookId, userId, editionId, Timestamp.from(NOW), Timestamp.from(NOW), Timestamp.from(NOW));
        return userBookId;
    }

    private void insertActiveSession(UUID userId, UUID userBookId) {
        jdbcTemplate.update("""
                INSERT INTO reading_sessions (
                    id, user_id, user_book_id, start_page, current_page, started_at, paused_seconds
                ) VALUES (?, ?, ?, 0, 0, ?, 0)
                """, UUID.randomUUID(), userId, userBookId, Timestamp.from(NOW));
    }
}

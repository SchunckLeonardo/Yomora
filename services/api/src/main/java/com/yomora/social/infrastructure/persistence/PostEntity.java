package com.yomora.social.infrastructure.persistence;

import com.yomora.social.domain.Post;
import com.yomora.social.domain.PostType;
import com.yomora.social.domain.Visibility;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "posts")
class PostEntity {
    @Id
    UUID id;
    @Column(name = "author_id")
    UUID authorId;
    String text;
    @Column(name = "edition_id")
    UUID editionId;
    @Enumerated(EnumType.STRING)
    PostType type;
    boolean spoiler;
    @Column(name = "spoiler_page")
    Integer spoilerPage;
    @Enumerated(EnumType.STRING)
    Visibility visibility;
    @Column(name = "created_at")
    Instant createdAt;
    @Column(name = "updated_at")
    Instant updatedAt;

    protected PostEntity() {
    }

    private PostEntity(Post post) {
        this.id = post.id();
        this.authorId = post.authorId();
        this.text = post.text();
        this.editionId = post.editionId();
        this.type = post.type();
        this.spoiler = post.spoiler();
        this.spoilerPage = post.spoilerPage();
        this.visibility = post.visibility();
        this.createdAt = post.createdAt();
        this.updatedAt = post.updatedAt();
    }

    static PostEntity from(Post post) {
        return new PostEntity(post);
    }

    Post toDomain(long likes, long comments) {
        return new Post(
                id, authorId, text, editionId, type, spoiler, spoilerPage, visibility,
                createdAt, updatedAt, likes, comments
        );
    }
}

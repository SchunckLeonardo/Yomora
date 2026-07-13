package com.yomora.social.infrastructure.persistence;

import com.yomora.social.domain.Comment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "comments")
class CommentEntity {
    @Id
    UUID id;
    @Column(name = "post_id")
    UUID postId;
    @Column(name = "author_id")
    UUID authorId;
    String text;
    @Column(name = "created_at")
    Instant createdAt;

    protected CommentEntity() {
    }

    private CommentEntity(Comment comment) {
        this.id = comment.id();
        this.postId = comment.postId();
        this.authorId = comment.authorId();
        this.text = comment.text();
        this.createdAt = comment.createdAt();
    }

    static CommentEntity from(Comment comment) {
        return new CommentEntity(comment);
    }

    Comment toDomain() {
        return new Comment(id, postId, authorId, text, createdAt);
    }
}

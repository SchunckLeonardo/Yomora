package com.yomora.identity.infrastructure.persistence;

import com.yomora.identity.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
class UserEntity {
    @Id
    UUID id;
    String name;
    String username;
    String email;
    @Column(name = "password_hash")
    String passwordHash;
    String bio;
    @Column(name = "avatar_url")
    String avatarUrl;
    @Column(name = "public_profile")
    boolean publicProfile;
    @Column(name = "created_at")
    Instant createdAt;
    @Column(name = "updated_at")
    Instant updatedAt;

    protected UserEntity() {
    }

    private UserEntity(User user) {
        this.id = user.id();
        this.name = user.name();
        this.username = user.username();
        this.email = user.email();
        this.passwordHash = user.passwordHash();
        this.bio = user.bio();
        this.avatarUrl = user.avatarUrl();
        this.publicProfile = user.publicProfile();
        this.createdAt = user.createdAt();
        this.updatedAt = user.updatedAt();
    }

    static UserEntity from(User user) {
        return new UserEntity(user);
    }

    User toDomain() {
        return new User(id, name, username, email, passwordHash, bio, avatarUrl, publicProfile, createdAt, updatedAt);
    }
}

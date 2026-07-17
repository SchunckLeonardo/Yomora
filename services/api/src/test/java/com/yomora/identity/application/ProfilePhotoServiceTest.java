package com.yomora.identity.application;

import com.yomora.identity.domain.User;
import com.yomora.identity.domain.UserRepository;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfilePhotoServiceTest {
    private static final Instant NOW = Instant.parse("2026-07-17T12:00:00Z");
    private final InMemoryUserRepository users = new InMemoryUserRepository();
    private final FakePhotoStorage storage = new FakePhotoStorage();
    private final ProfilePhotoService service = new ProfilePhotoService(
            users, storage, Clock.fixed(NOW, ZoneOffset.UTC)
    );

    @Test
    void createsAPrivateUploadAndStoresOnlyTheOwnedObjectKeyAfterUpload() {
        User user = users.save(user());

        ProfilePhotoUpload upload = service.requestUpload(user.id(), "image/jpeg", 1_024);
        storage.existingKey = upload.objectKey();
        User updated = service.completeUpload(user.id(), upload.objectKey());

        assertThat(upload.objectKey()).startsWith("avatars/" + user.id() + "/");
        assertThat(updated.avatarUrl()).isEqualTo(upload.objectKey());
        assertThat(service.readUrl(updated.avatarUrl())).isEqualTo("https://signed.example/" + upload.objectKey());
    }

    @Test
    void rejectsForeignMissingAndUnsupportedUploads() {
        User user = users.save(user());
        UUID otherUser = UUID.randomUUID();

        assertThatThrownBy(() -> service.completeUpload(user.id(), "avatars/" + otherUser + "/photo.jpg"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.completeUpload(user.id(), "avatars/" + user.id() + "/missing.jpg"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.requestUpload(user.id(), "image/svg+xml", 1_024))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.requestUpload(user.id(), "image/jpeg", 6 * 1024 * 1024))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private User user() {
        return new User(UUID.randomUUID(), "Marina", "marina", "marina@example.com", "hash", "", null,
                true, NOW.minusSeconds(60), NOW.minusSeconds(60));
    }

    private static final class FakePhotoStorage implements ProfilePhotoStorage {
        private String existingKey;

        @Override
        public ProfilePhotoUpload createUpload(UUID userId, String contentType, long contentLength, Instant expiresAt) {
            String key = "avatars/" + userId + "/photo.jpg";
            return new ProfilePhotoUpload(key, URI.create("https://upload.example/" + key), expiresAt);
        }

        @Override
        public boolean exists(String objectKey) {
            return objectKey.equals(existingKey);
        }

        @Override
        public String createReadUrl(String objectKey, Instant expiresAt) {
            return "https://signed.example/" + objectKey;
        }
    }

    private static final class InMemoryUserRepository implements UserRepository {
        private final Map<UUID, User> values = new HashMap<>();
        @Override public User save(User user) { values.put(user.id(), user); return user; }
        @Override public boolean existsByEmail(String email) { return false; }
        @Override public boolean existsByUsername(String username) { return false; }
        @Override public Optional<User> findByEmail(String email) { return Optional.empty(); }
        @Override public Optional<User> findById(UUID id) { return Optional.ofNullable(values.get(id)); }
        @Override public void deleteById(UUID id) { values.remove(id); }
    }
}

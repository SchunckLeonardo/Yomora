package com.yomora.identity.application;

import java.time.Instant;
import java.util.UUID;

public interface ProfilePhotoStorage extends AutoCloseable {
    ProfilePhotoUpload createUpload(UUID userId, String contentType, long contentLength, Instant expiresAt);

    boolean exists(String objectKey);

    String createReadUrl(String objectKey, Instant expiresAt);

    @Override
    default void close() {
    }
}

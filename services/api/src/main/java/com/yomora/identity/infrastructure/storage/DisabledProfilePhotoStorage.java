package com.yomora.identity.infrastructure.storage;

import com.yomora.identity.application.ProfilePhotoStorage;
import com.yomora.identity.application.ProfilePhotoUpload;

import java.time.Instant;
import java.util.UUID;

final class DisabledProfilePhotoStorage implements ProfilePhotoStorage {
    @Override
    public ProfilePhotoUpload createUpload(UUID userId, String contentType, long contentLength, Instant expiresAt) {
        throw new ProfilePhotoStorageUnavailableException();
    }

    @Override
    public boolean exists(String objectKey) {
        throw new ProfilePhotoStorageUnavailableException();
    }

    @Override
    public String createReadUrl(String objectKey, Instant expiresAt) {
        throw new ProfilePhotoStorageUnavailableException();
    }
}

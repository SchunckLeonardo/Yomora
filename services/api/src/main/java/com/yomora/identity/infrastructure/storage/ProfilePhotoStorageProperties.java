package com.yomora.identity.infrastructure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("yomora.storage.profile-photos")
public record ProfilePhotoStorageProperties(
        String bucket,
        String region,
        String endpoint,
        boolean pathStyleAccess
) {
}

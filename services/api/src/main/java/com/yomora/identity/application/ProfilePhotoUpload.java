package com.yomora.identity.application;

import java.net.URI;
import java.time.Instant;

public record ProfilePhotoUpload(String objectKey, URI uploadUrl, Instant expiresAt) {
}

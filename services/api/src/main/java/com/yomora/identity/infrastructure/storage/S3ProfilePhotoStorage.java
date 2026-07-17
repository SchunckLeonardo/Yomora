package com.yomora.identity.infrastructure.storage;

import com.yomora.identity.application.ProfilePhotoStorage;
import com.yomora.identity.application.ProfilePhotoUpload;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

final class S3ProfilePhotoStorage implements ProfilePhotoStorage, AutoCloseable {
    private final String bucket;
    private final S3Client client;
    private final S3Presigner presigner;

    S3ProfilePhotoStorage(String bucket, S3Client client, S3Presigner presigner) {
        this.bucket = bucket;
        this.client = client;
        this.presigner = presigner;
    }

    @Override
    public ProfilePhotoUpload createUpload(UUID userId, String contentType, long contentLength, Instant expiresAt) {
        String extension = switch (contentType) {
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> "jpg";
        };
        String objectKey = "avatars/" + userId + "/" + UUID.randomUUID() + "." + extension;
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .build();
        PutObjectPresignRequest request = PutObjectPresignRequest.builder()
                .signatureDuration(durationUntil(expiresAt))
                .putObjectRequest(objectRequest)
                .build();
        return new ProfilePhotoUpload(
                objectKey,
                java.net.URI.create(presigner.presignPutObject(request).url().toString()),
                expiresAt
        );
    }

    @Override
    public boolean exists(String objectKey) {
        try {
            client.headObject(HeadObjectRequest.builder().bucket(bucket).key(objectKey).build());
            return true;
        } catch (S3Exception exception) {
            if (exception.statusCode() == 404) {
                return false;
            }
            throw exception;
        }
    }

    @Override
    public String createReadUrl(String objectKey, Instant expiresAt) {
        GetObjectRequest objectRequest = GetObjectRequest.builder().bucket(bucket).key(objectKey).build();
        GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(durationUntil(expiresAt))
                .getObjectRequest(objectRequest)
                .build();
        return presigner.presignGetObject(request).url().toString();
    }

    private Duration durationUntil(Instant expiresAt) {
        Duration duration = Duration.between(Instant.now(), expiresAt);
        return duration.compareTo(Duration.ofSeconds(1)) < 0 ? Duration.ofSeconds(1) : duration;
    }

    @Override
    public void close() {
        presigner.close();
        client.close();
    }
}

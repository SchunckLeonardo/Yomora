package com.yomora.identity.infrastructure.storage;

import com.yomora.identity.application.ProfilePhotoUpload;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class S3ProfilePhotoStorageTest {

    @Test
    void presignedUploadDoesNotRequireURLSessionToReproduceContentLengthHeader() {
        StaticCredentialsProvider credentials = StaticCredentialsProvider.create(
                AwsBasicCredentials.create("test-access-key", "test-secret-key")
        );
        S3Client client = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentials)
                .build();
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentials)
                .build();

        try (S3ProfilePhotoStorage storage = new S3ProfilePhotoStorage("yomora-test", client, presigner)) {
            ProfilePhotoUpload upload = storage.createUpload(
                    UUID.randomUUID(),
                    "image/jpeg",
                    1_024,
                    Instant.now().plusSeconds(600)
            );

            String decodedQuery = URLDecoder.decode(upload.uploadUrl().getRawQuery(), StandardCharsets.UTF_8);
            assertThat(decodedQuery).contains("X-Amz-SignedHeaders=content-type;host");
            assertThat(decodedQuery).doesNotContain("content-length");
        }
    }
}

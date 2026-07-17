package com.yomora.identity.infrastructure.storage;

import com.yomora.identity.application.ProfilePhotoStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration(proxyBeanMethods = false)
class ProfilePhotoStorageConfiguration {
    @Bean(destroyMethod = "close")
    ProfilePhotoStorage profilePhotoStorage(ProfilePhotoStorageProperties properties) {
        if (properties.bucket() == null || properties.bucket().isBlank()) {
            return new DisabledProfilePhotoStorage();
        }
        Region region = Region.of(properties.region());
        S3Configuration serviceConfiguration = S3Configuration.builder()
                .pathStyleAccessEnabled(properties.pathStyleAccess())
                .build();
        var clientBuilder = S3Client.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .serviceConfiguration(serviceConfiguration);
        var presignerBuilder = S3Presigner.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .serviceConfiguration(serviceConfiguration);
        if (properties.endpoint() != null && !properties.endpoint().isBlank()) {
            URI endpoint = URI.create(properties.endpoint());
            clientBuilder.endpointOverride(endpoint);
            presignerBuilder.endpointOverride(endpoint);
        }
        return new S3ProfilePhotoStorage(properties.bucket(), clientBuilder.build(), presignerBuilder.build());
    }
}

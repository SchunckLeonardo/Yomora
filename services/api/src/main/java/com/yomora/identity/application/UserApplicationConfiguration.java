package com.yomora.identity.application;

import com.yomora.identity.domain.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class UserApplicationConfiguration {
    @Bean
    UserRegistrationService userRegistrationService(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            Clock clock
    ) {
        return new UserRegistrationService(repository, passwordEncoder, clock);
    }

    @Bean
    ProfilePhotoService profilePhotoService(
            UserRepository repository,
            ProfilePhotoStorage storage,
            Clock clock
    ) {
        return new ProfilePhotoService(repository, storage, clock);
    }
}

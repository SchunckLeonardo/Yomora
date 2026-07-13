package com.yomora.social.infrastructure.config;

import com.yomora.social.application.SocialService;
import com.yomora.social.domain.SocialRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
class SocialConfiguration {
    @Bean
    SocialService socialService(SocialRepository repository, Clock clock) {
        return new SocialService(repository, clock);
    }
}

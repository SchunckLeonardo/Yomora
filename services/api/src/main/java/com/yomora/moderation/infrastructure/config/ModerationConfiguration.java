package com.yomora.moderation.infrastructure.config;

import com.yomora.moderation.application.ModerationService;
import com.yomora.moderation.domain.ModerationRepository;
import com.yomora.social.domain.SocialRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
class ModerationConfiguration {
    @Bean
    ModerationService moderationService(
            ModerationRepository repository,
            SocialRepository socialRepository,
            Clock clock
    ) {
        return new ModerationService(repository, socialRepository::removeConnectionsBetween, clock);
    }
}

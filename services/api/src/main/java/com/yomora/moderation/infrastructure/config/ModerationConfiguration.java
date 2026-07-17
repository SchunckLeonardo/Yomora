package com.yomora.moderation.infrastructure.config;

import com.yomora.moderation.application.AdminAccessPolicy;
import com.yomora.moderation.application.AdminModerationService;
import com.yomora.moderation.application.CommunitySuspensionService;
import com.yomora.moderation.application.ModerationService;
import com.yomora.moderation.domain.CommunitySuspensionRepository;
import com.yomora.moderation.domain.ModerationRepository;
import com.yomora.social.domain.SocialRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.Set;

@Configuration(proxyBeanMethods = false)
class ModerationConfiguration {
    @Bean
    AdminAccessPolicy adminAccessPolicy(ModerationProperties properties) {
        return new AdminAccessPolicy(Set.copyOf(properties.adminUserIds()));
    }

    @Bean
    ModerationService moderationService(
            ModerationRepository repository,
            SocialRepository socialRepository,
            Clock clock
    ) {
        return new ModerationService(repository, socialRepository::removeConnectionsBetween, clock);
    }

    @Bean
    AdminModerationService adminModerationService(
            ModerationRepository repository,
            AdminAccessPolicy adminAccessPolicy
    ) {
        return new AdminModerationService(repository, adminAccessPolicy);
    }

    @Bean
    CommunitySuspensionService communitySuspensionService(
            CommunitySuspensionRepository repository,
            AdminAccessPolicy adminAccessPolicy,
            Clock clock
    ) {
        return new CommunitySuspensionService(repository, adminAccessPolicy, clock);
    }
}

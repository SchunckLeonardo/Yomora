package com.yomora.social.infrastructure.config;

import com.yomora.identity.domain.UserRepository;
import com.yomora.moderation.domain.ModerationRepository;
import com.yomora.social.application.InvalidFollowException;
import com.yomora.social.application.SocialService;
import com.yomora.social.domain.SocialRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
class SocialConfiguration {
    @Bean
    SocialService socialService(
            SocialRepository repository,
            UserRepository userRepository,
            ModerationRepository moderationRepository,
            Clock clock
    ) {
        return new SocialService(
                repository,
                userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new InvalidFollowException("Usuário não encontrado"))
                        .publicProfile(),
                moderationRepository::isBlockedEitherWay,
                clock
        );
    }
}

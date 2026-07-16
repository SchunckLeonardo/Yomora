package com.yomora.identity.infrastructure.security;

import com.yomora.identity.application.ExternalAuthenticationUnavailableException;
import com.yomora.identity.application.ExternalIdentityAuthenticator;
import com.yomora.identity.application.ExternalTokenRevoker;
import com.yomora.identity.application.ExternalAccountEventVerifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class ExternalIdentityConfiguration {
    @Bean
    @ConditionalOnMissingBean(ExternalIdentityAuthenticator.class)
    ExternalIdentityAuthenticator unavailableExternalIdentityAuthenticator() {
        return command -> {
            throw new ExternalAuthenticationUnavailableException();
        };
    }

    @Bean
    @ConditionalOnMissingBean(ExternalTokenRevoker.class)
    ExternalTokenRevoker unavailableExternalTokenRevoker() {
        return (provider, refreshToken) -> {
            throw new ExternalAuthenticationUnavailableException();
        };
    }

    @Bean
    @ConditionalOnMissingBean(ExternalAccountEventVerifier.class)
    ExternalAccountEventVerifier unavailableExternalAccountEventVerifier() {
        return payload -> {
            throw new ExternalAuthenticationUnavailableException();
        };
    }
}

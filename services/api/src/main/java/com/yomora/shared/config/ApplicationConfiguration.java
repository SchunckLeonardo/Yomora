package com.yomora.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class ApplicationConfiguration {
    @Bean
    Clock applicationClock() {
        return Clock.systemUTC();
    }
}

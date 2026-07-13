package com.yomora.review.infrastructure.config;

import com.yomora.review.application.NoteService;
import com.yomora.review.application.ReviewService;
import com.yomora.review.application.SocialPublisher;
import com.yomora.review.domain.NoteRepository;
import com.yomora.review.domain.ReviewRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
class ReviewConfiguration {
    @Bean
    ReviewService reviewService(ReviewRepository repository, SocialPublisher publisher, Clock clock) {
        return new ReviewService(repository, publisher, clock);
    }

    @Bean
    NoteService noteService(NoteRepository repository, SocialPublisher publisher, Clock clock) {
        return new NoteService(repository, publisher, clock);
    }
}

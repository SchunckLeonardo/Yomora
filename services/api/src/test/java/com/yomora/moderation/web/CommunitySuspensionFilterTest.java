package com.yomora.moderation.web;

import com.yomora.moderation.application.AdminAccessPolicy;
import com.yomora.moderation.application.CommunitySuspensionService;
import com.yomora.moderation.domain.CommunitySuspension;
import com.yomora.moderation.domain.CommunitySuspensionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CommunitySuspensionFilterTest {
    private static final Instant NOW = Instant.parse("2026-07-17T12:00:00Z");

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void blocksCommunityRoutesButKeepsReadingRoutesAvailable() throws Exception {
        UUID adminId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        InMemoryRepository repository = new InMemoryRepository();
        CommunitySuspensionService service = new CommunitySuspensionService(
                repository,
                new AdminAccessPolicy(Set.of(adminId)),
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
        service.suspend(adminId, userId, 7, "Assédio");
        CommunitySuspensionFilter filter = new CommunitySuspensionFilter(service, new ObjectMapper());
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(userId.toString(), "", Set.of())
        );

        MockHttpServletResponse communityResponse = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest("GET", "/api/v1/posts/feed"), communityResponse,
                new MockFilterChain());
        MockHttpServletResponse readingResponse = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest("GET", "/api/v1/library"), readingResponse,
                new MockFilterChain());
        MockHttpServletResponse followRequestResponse = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest("GET", "/api/v1/users/me/follow-requests"),
                followRequestResponse, new MockFilterChain());

        assertThat(communityResponse.getStatus()).isEqualTo(403);
        assertThat(communityResponse.getContentAsString()).contains("COMMUNITY_SUSPENDED");
        assertThat(followRequestResponse.getStatus()).isEqualTo(403);
        assertThat(readingResponse.getStatus()).isEqualTo(200);
    }

    private static final class InMemoryRepository implements CommunitySuspensionRepository {
        private CommunitySuspension value;
        @Override public CommunitySuspension save(CommunitySuspension suspension) { value = suspension; return suspension; }
        @Override public Optional<CommunitySuspension> findById(UUID id) {
            return value != null && value.id().equals(id) ? Optional.of(value) : Optional.empty();
        }
        @Override public Optional<CommunitySuspension> findActiveByUserId(UUID userId, Instant now) {
            return value != null && value.userId().equals(userId) && value.reversedAt() == null
                    && value.endsAt().isAfter(now) ? Optional.of(value) : Optional.empty();
        }
    }
}

package com.yomora.moderation.web;

import com.yomora.moderation.application.CommunityAccess;
import com.yomora.moderation.application.CommunitySuspensionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class CommunitySuspensionFilter extends OncePerRequestFilter {
    private final CommunitySuspensionService suspensions;
    private final ObjectMapper objectMapper;

    public CommunitySuspensionFilter(CommunitySuspensionService suspensions, ObjectMapper objectMapper) {
        this.suspensions = suspensions;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !isCommunityPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        UUID userId;
        try {
            userId = UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException ignored) {
            filterChain.doFilter(request, response);
            return;
        }
        CommunityAccess access = suspensions.accessFor(userId);
        if (access.allowed()) {
            filterChain.doFilter(request, response);
            return;
        }
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Seu acesso à Comunidade está suspenso até " + access.suspendedUntil()
        );
        detail.setTitle("Comunidade temporariamente indisponível");
        detail.setType(URI.create("https://yomora.app/problems/community-suspended"));
        detail.setProperty("code", "COMMUNITY_SUSPENDED");
        detail.setProperty("suspendedUntil", access.suspendedUntil());
        detail.setProperty("reason", access.reason());
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), detail);
    }

    private boolean isCommunityPath(String path) {
        if (path.equals("/api/v1/community/access") || path.startsWith("/api/v1/admin/")) {
            return false;
        }
        if (path.startsWith("/api/v1/posts")
                || path.startsWith("/api/v1/community/")
                || path.startsWith("/api/v1/moderation")) {
            return true;
        }
        if (!path.startsWith("/api/v1/users/")) {
            return false;
        }
        return !path.equals("/api/v1/users/me") && !path.startsWith("/api/v1/users/me/avatar/");
    }
}

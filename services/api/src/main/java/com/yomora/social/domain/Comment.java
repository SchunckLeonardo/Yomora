package com.yomora.social.domain;

import java.time.Instant;
import java.util.UUID;

public record Comment(UUID id, UUID postId, UUID authorId, String text, Instant createdAt) {
}

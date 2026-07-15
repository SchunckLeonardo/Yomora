package com.yomora.shared.web;

import com.yomora.reading.application.ActiveReadingSessionExistsException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {
    @Test
    void activeReadingSessionConflictExposesAStableCodeAndSessionId() {
        UUID sessionId = UUID.randomUUID();

        var detail = new GlobalExceptionHandler().activeReadingSessionConflict(
                new ActiveReadingSessionExistsException(sessionId)
        );

        assertThat(detail.getStatus()).isEqualTo(409);
        assertThat(detail.getProperties())
                .containsEntry("code", "ACTIVE_READING_SESSION_EXISTS")
                .containsEntry("activeSessionId", sessionId);
    }
}

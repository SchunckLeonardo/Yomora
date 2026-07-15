package com.yomora.reading.web;

import com.yomora.reading.application.ReadingSessionService;
import com.yomora.reading.domain.ReadingSession;
import com.yomora.shared.security.CurrentUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReadingSessionControllerTest {
    @Test
    void activeReturnsNoContentWhenThereIsNoSession() {
        UUID userId = UUID.randomUUID();
        Authentication authentication = mock(Authentication.class);
        CurrentUser currentUser = mock(CurrentUser.class);
        ReadingSessionService service = mock(ReadingSessionService.class);
        when(currentUser.id(authentication)).thenReturn(userId);
        when(service.active(userId)).thenReturn(Optional.empty());
        ReadingSessionController controller = new ReadingSessionController(service, currentUser);

        var response = controller.active(authentication);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void activeAndLifecycleOperationsReturnThePersistedSession() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        Authentication authentication = mock(Authentication.class);
        CurrentUser currentUser = mock(CurrentUser.class);
        ReadingSessionService service = mock(ReadingSessionService.class);
        ReadingSession session = new ReadingSession(
                sessionId, userId, UUID.randomUUID(), 10, 15, null, null,
                Instant.parse("2026-07-15T10:00:00Z"), null, 0,
                null, null, null, null
        );
        when(currentUser.id(authentication)).thenReturn(userId);
        when(service.active(userId)).thenReturn(Optional.of(session));
        when(service.pause(userId, sessionId)).thenReturn(session);
        when(service.resume(userId, sessionId)).thenReturn(session);
        when(service.updateProgress(userId, sessionId, 15)).thenReturn(session);
        ReadingSessionController controller = new ReadingSessionController(service, currentUser);

        assertThat(controller.active(authentication).getBody()).isEqualTo(session);
        assertThat(controller.pause(authentication, sessionId)).isEqualTo(session);
        assertThat(controller.resume(authentication, sessionId)).isEqualTo(session);
        assertThat(controller.progress(
                authentication,
                sessionId,
                new ReadingSessionController.ProgressRequest(15)
        )).isEqualTo(session);
        verify(service).updateProgress(userId, sessionId, 15);
    }
}

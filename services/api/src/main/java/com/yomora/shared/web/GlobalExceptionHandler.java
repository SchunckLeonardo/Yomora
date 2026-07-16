package com.yomora.shared.web;

import com.yomora.identity.application.IdentityConflictException;
import com.yomora.identity.application.InvalidCredentialsException;
import com.yomora.identity.application.InvalidRefreshTokenException;
import com.yomora.identity.application.InvalidPasswordResetTokenException;
import com.yomora.identity.application.InvalidEmailVerificationTokenException;
import com.yomora.identity.application.InvalidExternalAuthorizationCodeException;
import com.yomora.identity.application.InvalidExternalIdentityTokenException;
import com.yomora.identity.application.IdentityLinkRequiredException;
import com.yomora.identity.application.ExternalAuthenticationUnavailableException;
import com.yomora.identity.application.LastAccessMethodException;
import com.yomora.identity.web.UserNotFoundException;
import com.yomora.library.application.InvalidBookProgressException;
import com.yomora.library.application.LibraryConflictException;
import com.yomora.library.application.LibraryEntryNotFoundException;
import com.yomora.library.application.ShelfNotFoundException;
import com.yomora.reading.application.ReadingSessionNotFoundException;
import com.yomora.reading.application.ActiveReadingSessionExistsException;
import com.yomora.social.application.ContentForbiddenException;
import com.yomora.social.application.ContentNotFoundException;
import com.yomora.social.application.InvalidFollowException;
import com.yomora.review.application.NoteNotFoundException;
import com.yomora.review.application.ReviewNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ActiveReadingSessionExistsException.class)
    ProblemDetail activeReadingSessionConflict(ActiveReadingSessionExistsException exception) {
        ProblemDetail detail = problem(HttpStatus.CONFLICT, "Sessão de leitura em andamento", exception.getMessage());
        detail.setProperty("code", "ACTIVE_READING_SESSION_EXISTS");
        detail.setProperty("activeSessionId", exception.activeSessionId());
        return detail;
    }

    @ExceptionHandler(IdentityConflictException.class)
    ProblemDetail conflict(IdentityConflictException exception) {
        return problem(HttpStatus.CONFLICT, "Conflito de identidade", exception.getMessage());
    }

    @ExceptionHandler({InvalidCredentialsException.class, InvalidRefreshTokenException.class,
            InvalidPasswordResetTokenException.class, InvalidExternalAuthorizationCodeException.class,
            InvalidExternalIdentityTokenException.class})
    ProblemDetail unauthorized(RuntimeException exception) {
        return problem(HttpStatus.UNAUTHORIZED, "Não autorizado", exception.getMessage());
    }

    @ExceptionHandler(IdentityLinkRequiredException.class)
    ProblemDetail identityLinkRequired(IdentityLinkRequiredException exception) {
        ProblemDetail detail = problem(HttpStatus.CONFLICT, "Confirmação da conta necessária", exception.getMessage());
        detail.setProperty("code", "IDENTITY_LINK_REQUIRED");
        return detail;
    }

    @ExceptionHandler(ExternalAuthenticationUnavailableException.class)
    ProblemDetail externalAuthenticationUnavailable(ExternalAuthenticationUnavailableException exception) {
        return problem(HttpStatus.SERVICE_UNAVAILABLE, "Login externo indisponível", exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    ProblemDetail notFound(UserNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "Recurso não encontrado", exception.getMessage());
    }

    @ExceptionHandler({
            LibraryEntryNotFoundException.class,
            ShelfNotFoundException.class,
            ReadingSessionNotFoundException.class,
            ReviewNotFoundException.class,
            NoteNotFoundException.class
    })
    ProblemDetail libraryNotFound(RuntimeException exception) {
        return problem(HttpStatus.NOT_FOUND, "Recurso não encontrado", exception.getMessage());
    }

    @ExceptionHandler(ContentNotFoundException.class)
    ProblemDetail contentNotFound(ContentNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "Conteúdo não encontrado", exception.getMessage());
    }

    @ExceptionHandler(ContentForbiddenException.class)
    ProblemDetail forbidden(ContentForbiddenException exception) {
        return problem(HttpStatus.FORBIDDEN, "Operação não permitida", exception.getMessage());
    }

    @ExceptionHandler(LastAccessMethodException.class)
    ProblemDetail lastAccessMethod(LastAccessMethodException exception) {
        return problem(HttpStatus.CONFLICT, "Último método de acesso", exception.getMessage());
    }

    @ExceptionHandler(InvalidFollowException.class)
    ProblemDetail invalidFollow(InvalidFollowException exception) {
        return problem(HttpStatus.BAD_REQUEST, "Operação de seguimento inválida", exception.getMessage());
    }

    @ExceptionHandler(LibraryConflictException.class)
    ProblemDetail libraryConflict(LibraryConflictException exception) {
        return problem(HttpStatus.CONFLICT, "Conflito na biblioteca", exception.getMessage());
    }

    @ExceptionHandler({InvalidBookProgressException.class, InvalidEmailVerificationTokenException.class,
            IllegalArgumentException.class})
    ProblemDetail invalidRule(RuntimeException exception) {
        return problem(HttpStatus.BAD_REQUEST, "Operação inválida", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(MethodArgumentNotValidException exception) {
        ProblemDetail detail = problem(HttpStatus.BAD_REQUEST, "Dados inválidos", "Corrija os campos informados");
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        detail.setProperty("errors", errors);
        return detail;
    }

    private ProblemDetail problem(HttpStatus status, String title, String detailMessage) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, detailMessage);
        detail.setTitle(title);
        detail.setType(URI.create("https://yomora.app/problems/" + status.value()));
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }
}

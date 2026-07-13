package com.yomora.review.web;

import com.yomora.review.application.NoteService;
import com.yomora.review.domain.Note;
import com.yomora.shared.security.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notes")
class NoteController {
    private final NoteService service;
    private final CurrentUser currentUser;

    NoteController(NoteService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @PostMapping
    ResponseEntity<Note> create(Authentication authentication, @Valid @RequestBody NoteRequest request) {
        Note note = service.create(
                currentUser.id(authentication), request.editionId(), request.content(), request.page(),
                request.chapter(), request.privateNote(), request.spoiler()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    @GetMapping
    List<Note> list(Authentication authentication, @RequestParam UUID editionId) {
        return service.list(currentUser.id(authentication), editionId);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(Authentication authentication, @PathVariable UUID id) {
        service.delete(currentUser.id(authentication), id);
        return ResponseEntity.noContent().build();
    }

    record NoteRequest(
            @NotNull UUID editionId,
            @NotBlank @Size(max = 5000) String content,
            @Min(0) Integer page,
            @Size(max = 200) String chapter,
            boolean privateNote,
            boolean spoiler
    ) {
    }
}

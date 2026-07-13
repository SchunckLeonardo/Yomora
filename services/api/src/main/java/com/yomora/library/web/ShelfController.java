package com.yomora.library.web;

import com.yomora.library.application.LibraryService;
import com.yomora.library.application.ShelfService;
import com.yomora.library.domain.Shelf;
import com.yomora.shared.security.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shelves")
class ShelfController {
    private final ShelfService service;
    private final LibraryService libraryService;
    private final CurrentUser currentUser;

    ShelfController(ShelfService service, LibraryService libraryService, CurrentUser currentUser) {
        this.service = service;
        this.libraryService = libraryService;
        this.currentUser = currentUser;
    }

    @GetMapping
    List<Shelf> list(Authentication authentication) {
        return service.list(currentUser.id(authentication));
    }

    @PostMapping
    ResponseEntity<Shelf> create(Authentication authentication, @Valid @RequestBody ShelfRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(currentUser.id(authentication), request.name(), request.publicShelf()));
    }

    @PatchMapping("/{id}")
    Shelf update(Authentication authentication, @PathVariable UUID id, @Valid @RequestBody ShelfRequest request) {
        return service.rename(currentUser.id(authentication), id, request.name(), request.publicShelf());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(Authentication authentication, @PathVariable UUID id) {
        service.delete(currentUser.id(authentication), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/books")
    Shelf addBook(Authentication authentication, @PathVariable UUID id, @Valid @RequestBody ShelfBookRequest request) {
        UUID userId = currentUser.id(authentication);
        libraryService.get(userId, request.userBookId());
        return service.addBook(userId, id, request.userBookId());
    }

    @DeleteMapping("/{id}/books/{userBookId}")
    Shelf removeBook(Authentication authentication, @PathVariable UUID id, @PathVariable UUID userBookId) {
        return service.removeBook(currentUser.id(authentication), id, userBookId);
    }

    record ShelfRequest(@NotBlank @Size(max = 80) String name, boolean publicShelf) {
    }

    record ShelfBookRequest(@NotNull UUID userBookId) {
    }
}

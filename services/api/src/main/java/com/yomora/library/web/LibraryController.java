package com.yomora.library.web;

import com.yomora.library.application.LibraryService;
import com.yomora.library.domain.ReadingStatus;
import com.yomora.library.domain.UserBook;
import com.yomora.shared.security.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/library")
class LibraryController {
    private final LibraryService service;
    private final CurrentUser currentUser;

    LibraryController(LibraryService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @GetMapping
    List<UserBook> list(
            Authentication authentication,
            @RequestParam(required = false) ReadingStatus status,
            @RequestParam(defaultValue = "addedAt") String sort
    ) {
        List<UserBook> entries = service.list(currentUser.id(authentication), status);
        Comparator<UserBook> comparator = switch (sort) {
            case "progress" -> Comparator.comparingInt(UserBook::currentPage).reversed();
            case "status" -> Comparator.comparing(entry -> entry.status().name());
            default -> Comparator.comparing(UserBook::createdAt).reversed();
        };
        return entries.stream().sorted(comparator).toList();
    }

    @PostMapping("/books")
    ResponseEntity<UserBook> add(Authentication authentication, @Valid @RequestBody AddBookRequest request) {
        UserBook entry = service.add(
                currentUser.id(authentication), request.editionId(), request.status(), request.targetFinishDate()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(entry);
    }

    @PatchMapping("/books/{id}")
    UserBook update(
            Authentication authentication,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBookRequest request
    ) {
        return service.update(
                currentUser.id(authentication), id, request.status(), request.currentPage(),
                request.rating(), request.targetFinishDate()
        );
    }

    @DeleteMapping("/books/{id}")
    ResponseEntity<Void> remove(Authentication authentication, @PathVariable UUID id) {
        service.remove(currentUser.id(authentication), id);
        return ResponseEntity.noContent().build();
    }

    record AddBookRequest(
            @NotNull UUID editionId,
            ReadingStatus status,
            LocalDate targetFinishDate
    ) {
    }

    record UpdateBookRequest(
            ReadingStatus status,
            @Min(0) Integer currentPage,
            @Min(1) @Max(5) Integer rating,
            LocalDate targetFinishDate
    ) {
    }
}

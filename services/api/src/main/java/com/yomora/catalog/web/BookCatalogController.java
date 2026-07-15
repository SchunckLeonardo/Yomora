package com.yomora.catalog.web;

import com.yomora.catalog.application.BookCatalogAggregator;
import com.yomora.catalog.application.SearchBooksQuery;
import com.yomora.catalog.domain.BookCandidate;
import com.yomora.catalog.domain.BookCatalogRepository;
import com.yomora.catalog.domain.BookSearchResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
class BookCatalogController {
    private final BookCatalogAggregator aggregator;
    private final BookCatalogRepository repository;

    BookCatalogController(BookCatalogAggregator aggregator, BookCatalogRepository repository) {
        this.aggregator = aggregator;
        this.repository = repository;
    }

    @GetMapping("/search")
    List<BookSearchResult> search(
            @RequestParam("q") @NotBlank String query,
            @RequestParam(defaultValue = "por") String language,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int limit
    ) {
        return aggregator.search(new SearchBooksQuery(query, language, limit));
    }

    @GetMapping("/{editionId}")
    BookSearchResult edition(@PathVariable UUID editionId) {
        return repository.findEdition(editionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Edição não encontrada"));
    }

    @PostMapping("/manual")
    ResponseEntity<BookSearchResult> manual(@Valid @RequestBody ManualBookRequest request) {
        BookSearchResult result = repository.save(new BookCandidate(
                request.title(),
                request.description() == null ? "" : request.description(),
                request.authors(),
                request.categories() == null ? List.of() : request.categories(),
                request.isbn10(),
                request.isbn13(),
                request.publisher(),
                request.publicationDate(),
                request.language(),
                request.pageCount(),
                request.coverUrl(),
                "manual",
                UUID.randomUUID().toString()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    record ManualBookRequest(
            @NotBlank @Size(max = 500) String title,
            @Size(max = 10_000) String description,
            @Size(min = 1) List<@NotBlank String> authors,
            List<String> categories,
            String isbn10,
            String isbn13,
            @Size(max = 200) String publisher,
            LocalDate publicationDate,
            @NotBlank @Size(max = 10) String language,
            @Min(1) Integer pageCount,
            @Size(max = 1000) String coverUrl
    ) {
    }
}

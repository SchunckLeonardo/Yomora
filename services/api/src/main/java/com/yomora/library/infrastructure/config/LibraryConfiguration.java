package com.yomora.library.infrastructure.config;

import com.yomora.catalog.domain.BookCatalogRepository;
import com.yomora.library.application.LibraryService;
import com.yomora.library.application.ShelfService;
import com.yomora.library.domain.ShelfRepository;
import com.yomora.library.domain.UserBookRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
class LibraryConfiguration {
    @Bean
    LibraryService libraryService(
            UserBookRepository repository,
            BookCatalogRepository catalogRepository,
            Clock clock
    ) {
        return new LibraryService(repository, catalogRepository, clock);
    }

    @Bean
    ShelfService shelfService(ShelfRepository repository, Clock clock) {
        return new ShelfService(repository, clock);
    }
}

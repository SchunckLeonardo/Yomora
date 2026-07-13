package com.yomora.catalog.infrastructure.config;

import com.yomora.catalog.application.BookCatalogAggregator;
import com.yomora.catalog.domain.BookCatalogRepository;
import com.yomora.catalog.domain.BookProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration(proxyBeanMethods = false)
class CatalogConfiguration {
    @Bean
    BookCatalogAggregator bookCatalogAggregator(
            BookCatalogRepository repository,
            @Qualifier("googleBooksProvider") BookProvider googleBooks,
            @Qualifier("openLibraryProvider") BookProvider openLibrary
    ) {
        return new BookCatalogAggregator(repository, googleBooks, openLibrary);
    }
}

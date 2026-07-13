package com.yomora.catalog.domain;

import java.util.List;

@FunctionalInterface
public interface BookProvider {
    List<BookCandidate> search(BookProviderQuery query);
}

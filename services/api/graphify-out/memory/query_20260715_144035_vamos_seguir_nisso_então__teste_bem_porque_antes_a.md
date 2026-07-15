---
type: "implementation"
date: "2026-07-15T14:40:35.118160+00:00"
question: "Vamos seguir nisso então, teste bem porque antes a gente teve que trocar pt por por, porque era o formato aceito. Então veja o que funciona."
contributor: "graphify"
outcome: "useful"
source_nodes: ["BookCatalogAggregator", "GoogleBooksProvider", "OpenLibraryProvider", "BookCatalogRepository"]
---

# Q: Vamos seguir nisso então, teste bem porque antes a gente teve que trocar pt por por, porque era o formato aceito. Então veja o que funciona.

## Answer

Expanded via graph vocabulary: aggregator, book, catalog, google, open, library, provider, query, search, fallback, isbn, deduplicates. Implemented federated Google Books plus Open Library search. Both providers are always queried, exact author matches are ranked first, and editions are deduplicated. Internal and persisted language uses ISO 639-3 por; Google Books receives langRestrict=pt; Open Library receives lang=pt as a ranking preference instead of language=por as an exclusion filter. Live Open Library checks for Paul Washer returned 44 general matches with lang=pt, 42 author matches, and only one result with language=por. Targeted tests were red before implementation and green afterward; the complete Spring suite passed after repository language normalization.

## Outcome

- Signal: useful

## Source Nodes

- BookCatalogAggregator
- GoogleBooksProvider
- OpenLibraryProvider
- BookCatalogRepository
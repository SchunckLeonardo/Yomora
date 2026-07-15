---
type: "architecture"
date: "2026-07-15T14:25:19.068935+00:00"
question: "Existe uma API de livros mais completa para buscas como Paul Washer?"
contributor: "graphify"
outcome: "useful"
source_nodes: ["BookCatalogAggregator", "GoogleBooksProvider", "OpenLibraryProvider"]
---

# Q: Existe uma API de livros mais completa para buscas como Paul Washer?

## Answer

O Yomora já integra Google Books e Open Library. O problema principal é a estratégia atual: BookCatalogAggregator só chama Open Library quando Google retorna lista vazia; GoogleBooksProvider envia uma busca genérica com langRestrict. A consulta oficial Open Library por author=Paul Washer retornou 42 registros. Recomendação: consultar Google e Open Library em conjunto, adicionar variantes explícitas de busca por autor, não excluir outros idiomas na descoberta, ranquear o idioma solicitado e deduplicar por ISBN/título+autor. Avaliar um provedor comercial apenas como terceira fonte após medir lacunas.

## Outcome

- Signal: useful

## Source Nodes

- BookCatalogAggregator
- GoogleBooksProvider
- OpenLibraryProvider
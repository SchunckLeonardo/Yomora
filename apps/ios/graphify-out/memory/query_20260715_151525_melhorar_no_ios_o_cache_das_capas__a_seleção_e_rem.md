---
type: "query"
date: "2026-07-15T15:15:25.487253+00:00"
question: "Melhorar no iOS o cache das capas, a seleção e remoção de leituras pausadas ou abandonadas nas abas Hoje e Ler, e a visibilidade e persistência das notas durante o foco."
contributor: "graphify"
outcome: "useful"
source_nodes: ["BookCoverImageCache", "QuickReadViewModel", "LibraryBookDetailsView", "ReadingSessionViewModel", "ReadingSessionNoteField", "TodayView"]
---

# Q: Melhorar no iOS o cache das capas, a seleção e remoção de leituras pausadas ou abandonadas nas abas Hoje e Ler, e a visibilidade e persistência das notas durante o foco.

## Answer

BookCover passou a usar cache persistente em memória e disco; QuickReadViewModel carrega e permite selecionar somente leituras ativas; libraryDidChange atualiza Hoje, Ler e Biblioteca após mudança de status ou sessão; ReadingSessionView mantém o editor acima do teclado e as notas salvas aparecem no resumo e no histórico do livro.

## Outcome

- Signal: useful

## Source Nodes

- BookCoverImageCache
- QuickReadViewModel
- LibraryBookDetailsView
- ReadingSessionViewModel
- ReadingSessionNoteField
- TodayView
---
type: "query"
date: "2026-07-15T13:46:31.087290+00:00"
question: "Quando eu clico para finalizar sessão e aparece o resumo da leitura, e clico na seta para voltar ela volta para o timer em contagem ao invés de voltar para a tela \"Hoje\""
contributor: "graphify"
outcome: "useful"
source_nodes: ["MainTabView", "FeatureNavigation", "ReadingSessionView", "SessionSummaryView"]
---

# Q: Quando eu clico para finalizar sessão e aparece o resumo da leitura, e clico na seta para voltar ela volta para o timer em contagem ao invés de voltar para a tela "Hoje"

## Answer

Expanded from original query via graph vocab: [finish, summary, navigation, route, router, today, timer, session, view, state]. SessionSummaryView was presented as a child navigation destination of ReadingSessionView, so the default back action only removed the summary and revealed the finished timer. MainTabView now owns explicit tab selection, FeatureNavigation clears the active reading path, and the summary back action selects Today. The UI regression test verifies the Today tab and Yomora root after leaving the summary.

## Outcome

- Signal: useful

## Source Nodes

- MainTabView
- FeatureNavigation
- ReadingSessionView
- SessionSummaryView
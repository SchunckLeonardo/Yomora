---
type: "query"
date: "2026-07-15T13:35:09.928517+00:00"
question: "A parte de escrever uma nota sobre a sessão de leitura está quase invísivel, não da para saber o que está escrito no placeholder"
contributor: "graphify"
outcome: "useful"
source_nodes: ["ReadingSessionNoteField", "YomoraColor", "YomoraDesignTests"]
---

# Q: A parte de escrever uma nota sobre a sessão de leitura está quase invísivel, não da para saber o que está escrito no placeholder

## Answer

Expanded from original query via graph vocab: [reading, session, note, field, input, focus, color, contrast, text, view]. ReadingSessionNoteField used a white background without explicit foreground and prompt colors, so dark appearance produced nearly white placeholder text on white. Added fixed focus-input background, text, and placeholder tokens, enforced AA contrast by test, and verified note entry in the reading-session UI flow.

## Outcome

- Signal: useful

## Source Nodes

- ReadingSessionNoteField
- YomoraColor
- YomoraDesignTests
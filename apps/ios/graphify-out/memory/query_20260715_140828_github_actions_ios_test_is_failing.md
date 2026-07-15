---
type: "query"
date: "2026-07-15T14:08:28.914100+00:00"
question: "github actions ios test is failing"
contributor: "graphify"
outcome: "useful"
source_nodes: ["AuthenticationView", "MainTabView", "XCTest", "YomoraFlowUITests"]
---

# Q: github actions ios test is failing

## Answer

Expanded from original query via graph vocab: [authentication, login, test, tab, keyboard, community, app, view, selection, state]. GitHub Actions run 29420784957 showed all 18 unit tests and the reading summary UI flow passing; testCommentComposerStaysAboveSoftwareKeyboard failed before the keyboard assertions because it queried the Community tab with a 3-second timeout before the post-login TabBar appeared. YomoraFlowUITests now centralizes launch and login, waits up to 10 seconds for the first TabBar as the authenticated-screen readiness signal, then continues with short feature-specific waits. The formerly failing test passed three consecutive iterations and the full 20-test suite passed.

## Outcome

- Signal: useful

## Source Nodes

- AuthenticationView
- MainTabView
- XCTest
- YomoraFlowUITests
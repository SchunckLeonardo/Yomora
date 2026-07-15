# Graph Report - ios  (2026-07-15)

## Corpus Check
- 51 files · ~62,670 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 554 nodes · 1127 edges · 51 communities (17 shown, 34 thin omitted)
- Extraction: 89% EXTRACTED · 11% INFERRED · 0% AMBIGUOUS · INFERRED: 119 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `d0013311`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Equatable
- APIClientProtocol
- APIError
- SearchViewModel
- AppContainer
- ReadingTimer
- TokenPair
- StubAPIClient
- AppRoute
- ReadingStatus
- APIClient
- Yomora Application Target
- View
- TodayView
- URLProtocolStub
- SwiftUI
- BookComponents.swift
- TextDraft
- ButtonsAndFields.swift
- State
- SettingsView
- StatisticsView
- Void
- AnyEncodable
- Bool
- Data
- T
- URLRequest
- Data
- Int
- T
- Data
- Date
- ModelContainer
- UUID
- Date
- String
- Bool
- Date
- TimeInterval
- String
- UUID
- Date
- Int
- String
- Bool
- Data
- URLRequest
- Data
- String
- T

## God Nodes (most connected - your core abstractions)
1. `APIClientProtocol` - 41 edges
2. `Endpoint` - 40 edges
3. `Post` - 26 edges
4. `AppContainer` - 25 edges
5. `SwiftUI` - 20 edges
6. `TextDraft` - 18 edges
7. `Book` - 18 edges
8. `Foundation` - 17 edges
9. `Body` - 17 edges
10. `LibraryBook` - 17 edges

## Surprising Connections (you probably didn't know these)
- `Yomora Application Target` --references--> `Yomora App Icon Artwork`  [INFERRED]
  project.yml → Yomora/Resources/Assets.xcassets/AppIcon.appiconset/app-icon.png
- `Yomora Application Target` --references--> `Yomora Logo Artwork`  [INFERRED]
  project.yml → Yomora/Resources/Assets.xcassets/Logo.imageset/logo.png
- `Body` --implements--> `Encodable`  [EXTRACTED]
  Yomora/App/SessionStore.swift → YomoraTests/TestSupport.swift
- `TodayAPIClient` --implements--> `APIClientProtocol`  [EXTRACTED]
  YomoraTests/TodayViewModelTests.swift → Yomora/Core/Networking/APIClient.swift
- `FeedEngagementAPI` --references--> `Post`  [EXTRACTED]
  YomoraTests/TokenAndNavigationTests.swift → Yomora/Core/Networking/APIModels.swift

## Import Cycles
- None detected.

## Communities (51 total, 34 thin omitted)

### Community 0 - "Equatable"
Cohesion: 0.12
Nodes (46): Codable, Equatable, Hashable, Identifiable, Sendable, LibraryItem, QuickReadView, Book (+38 more)

### Community 1 - "APIClientProtocol"
Cohesion: 0.06
Nodes (34): SwiftData, SwiftUI, AppContainer, State, restoring, signedIn, signedOut, LibraryViewModel (+26 more)

### Community 2 - "APIError"
Cohesion: 0.12
Nodes (16): Set, FeedView, FeedKind, discover, following, FeedViewModel, State, error (+8 more)

### Community 3 - "SearchViewModel"
Cohesion: 0.07
Nodes (20): Encoder, Foundation, Observation, NoopReadingActivityManager, ReadingActivityManaging, ReadingTimer, Encodable, AnyEncodable (+12 more)

### Community 4 - "AppContainer"
Cohesion: 0.08
Nodes (27): FocusState, GeometryProxy, Notification, String, URLQueryItem, Body, init(), login() (+19 more)

### Community 5 - "ReadingTimer"
Cohesion: 0.06
Nodes (20): HTTPURLResponse, TodayViewModel, URLProtocol, APIClientTests, URLProtocolStub, ReadingTimerTests, SessionStoreTests, XCTest (+12 more)

### Community 6 - "TokenPair"
Cohesion: 0.10
Nodes (15): Any, App, Error, OSStatus, Scene, Security, URL, URLSession (+7 more)

### Community 7 - "StubAPIClient"
Cohesion: 0.05
Nodes (52): Axis, ButtonStyle, Configuration, PostType, PreviewProvider, UIKeyboardType, UITextContentType, View (+44 more)

### Community 8 - "AppRoute"
Cohesion: 0.09
Nodes (21): Content, AppRoute, book, composer, followers, libraryBook, post, profile (+13 more)

### Community 9 - "ReadingStatus"
Cohesion: 0.12
Nodes (13): ModelContext, BookCache, CachedBook, SearchViewModelTests, SearchBar, SearchView, SearchViewModel, State (+5 more)

### Community 10 - "APIClient"
Cohesion: 0.13
Nodes (14): Decodable, LocalizedError, APIError, invalidResponse, invalidURL, server, unauthorized, Method (+6 more)

### Community 11 - "Yomora Application Target"
Cohesion: 0.19
Nodes (13): iOS 17 Deployment Target, Local API Endpoint at Port 8080, Swift 6 Configuration, Yomora Application Target, Yomora iOS Project, Yomora UI Test Target, Yomora Unit Test Target, Yomora Book-and-Sprout Symbol (+5 more)

### Community 12 - "View"
Cohesion: 0.08
Nodes (29): CaseIterable, ColorScheme, Mode, UIKit, UInt, AccentChoice, forest, gold (+21 more)

### Community 13 - "TodayView"
Cohesion: 0.42
Nodes (4): UIUserInterfaceStyle, RGB, Double, YomoraDesignTests

### Community 17 - "TextDraft"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Quando eu clico para finalizar sessão e aparece o resumo da leitura, e clico na seta para voltar ela volta para o timer em contagem ao invés de voltar para a tela "Hoje", Source Nodes

### Community 19 - "State"
Cohesion: 0.15
Nodes (14): State, error, finished, finishing, idle, paused, running, starting (+6 more)

### Community 25 - "AnyEncodable"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: A parte de escrever uma nota sobre a sessão de leitura está quase invísivel, não da para saber o que está escrito no placeholder, Source Nodes

## Knowledge Gaps
- **89 isolated node(s):** `book`, `libraryBook`, `reading`, `sessionSummary`, `post` (+84 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **34 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `APIClientProtocol` connect `AppContainer` to `Equatable`, `APIClientProtocol`, `APIError`, `SearchViewModel`, `ReadingTimer`, `TokenPair`, `StubAPIClient`, `ReadingStatus`?**
  _High betweenness centrality (0.116) - this node is a cross-community bridge._
- **Why does `Endpoint` connect `AppContainer` to `Equatable`, `APIClientProtocol`, `APIError`, `ReadingTimer`, `TokenPair`, `ReadingStatus`, `APIClient`?**
  _High betweenness centrality (0.076) - this node is a cross-community bridge._
- **Why does `Post` connect `Equatable` to `APIError`, `AppContainer`, `StubAPIClient`?**
  _High betweenness centrality (0.061) - this node is a cross-community bridge._
- **Are the 12 inferred relationships involving `APIClientProtocol` (e.g. with `.add()` and `.comment()`) actually correct?**
  _`APIClientProtocol` has 12 INFERRED edges - model-reasoned connections that need verification._
- **Are the 25 inferred relationships involving `Endpoint` (e.g. with `login()` and `refreshUser()`) actually correct?**
  _`Endpoint` has 25 INFERRED edges - model-reasoned connections that need verification._
- **Are the 4 inferred relationships involving `AppContainer` (e.g. with `.save()` and `.follow()`) actually correct?**
  _`AppContainer` has 4 INFERRED edges - model-reasoned connections that need verification._
- **What connects `book`, `libraryBook`, `reading` to the rest of the system?**
  _89 weakly-connected nodes found - possible documentation gaps or missing edges._
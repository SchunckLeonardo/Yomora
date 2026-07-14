# Graph Report - ios  (2026-07-13)

## Corpus Check
- 47 files · ~59,896 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 454 nodes · 792 edges · 83 communities (14 shown, 69 thin omitted)
- Extraction: 90% EXTRACTED · 10% INFERRED · 0% AMBIGUOUS · INFERRED: 82 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `8128224d`
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
- AppRoute
- ReadingStatus
- Yomora Application Target
- View
- TodayView
- URLProtocolStub
- SwiftUI
- BookComponents.swift
- OnboardingView.swift
- ButtonsAndFields.swift
- ReadingSessionView
- FeatureNavigation
- SettingsView
- StatisticsView
- Void
- CGFloat
- Double
- Int
- String
- String
- Void
- CGFloat
- Int
- String
- Void
- CGFloat
- Double
- Bool
- Data
- T
- URLRequest
- Bool
- Double
- Int
- String
- UUID
- Data
- Int
- T
- T
- Void
- Data
- Date
- ModelContainer
- UUID
- Date
- String
- Bool
- Date
- TimeInterval
- SessionStore
- String
- String
- Int
- String
- UUID
- String
- Bool
- Int
- SessionStore
- String
- UUID
- String
- TimeInterval
- Date
- Int
- String
- SessionStore
- String
- Double
- Bool
- Data
- URLRequest
- Data
- String
- T

## God Nodes (most connected - your core abstractions)
1. `Endpoint` - 33 edges
2. `APIClientProtocol` - 29 edges
3. `SwiftUI` - 19 edges
4. `Foundation` - 17 edges
5. `Body` - 17 edges
6. `TokenPair` - 15 edges
7. `AppRoute` - 14 edges
8. `PostType` - 13 edges
9. `ReadingSessionViewModel` - 13 edges
10. `APIClient` - 12 edges

## Surprising Connections (you probably didn't know these)
- `Yomora Application Target` --references--> `Yomora App Icon Artwork`  [INFERRED]
  project.yml → Yomora/Resources/Assets.xcassets/AppIcon.appiconset/app-icon.png
- `Yomora Application Target` --references--> `Yomora Logo Artwork`  [INFERRED]
  project.yml → Yomora/Resources/Assets.xcassets/Logo.imageset/logo.png
- `Body` --implements--> `Encodable`  [EXTRACTED]
  Yomora/App/SessionStore.swift → YomoraTests/TestSupport.swift
- `StubAPIClient` --implements--> `APIClientProtocol`  [EXTRACTED]
  YomoraTests/TestSupport.swift → Yomora/Core/Networking/APIClient.swift
- `TodayAPIClient` --implements--> `APIClientProtocol`  [EXTRACTED]
  YomoraTests/TodayViewModelTests.swift → Yomora/Core/Networking/APIClient.swift

## Import Cycles
- None detected.

## Communities (83 total, 69 thin omitted)

### Community 0 - "Equatable"
Cohesion: 0.14
Nodes (37): Codable, Equatable, Hashable, Identifiable, Sendable, String, Book, Comment (+29 more)

### Community 1 - "APIClientProtocol"
Cohesion: 0.07
Nodes (21): App, Foundation, Scene, SwiftData, URL, URLSession, AppContainer, init() (+13 more)

### Community 2 - "APIError"
Cohesion: 0.09
Nodes (18): Decodable, Encoder, LocalizedError, APIError, invalidResponse, invalidURL, server, unauthorized (+10 more)

### Community 3 - "SearchViewModel"
Cohesion: 0.13
Nodes (12): ModelContext, BookCache, CachedBook, SearchView, SearchViewModelTests, SearchViewModel, State, error (+4 more)

### Community 4 - "AppContainer"
Cohesion: 0.15
Nodes (13): URLQueryItem, Body, login(), refreshUser(), register(), requestPasswordReset(), restore(), State (+5 more)

### Community 5 - "ReadingTimer"
Cohesion: 0.10
Nodes (11): HTTPURLResponse, URLProtocol, APIClientTests, URLProtocolStub, ReadingTimerTests, SessionStoreTests, TokenAndNavigationTests, XCTest (+3 more)

### Community 6 - "TokenPair"
Cohesion: 0.16
Nodes (9): Any, Error, OSStatus, Security, InMemoryTokenStore, KeychainError, KeychainTokenStore, TokenStoring (+1 more)

### Community 8 - "AppRoute"
Cohesion: 0.18
Nodes (10): AppRoute, book, composer, followers, libraryBook, post, profile, reading (+2 more)

### Community 9 - "ReadingStatus"
Cohesion: 0.07
Nodes (19): Observation, ReadingTimer, LibraryView, LibraryViewModel, State, error, idle, loaded (+11 more)

### Community 11 - "Yomora Application Target"
Cohesion: 0.19
Nodes (13): iOS 17 Deployment Target, Local API Endpoint at Port 8080, Swift 6 Configuration, Yomora Application Target, Yomora iOS Project, Yomora UI Test Target, Yomora Unit Test Target, Yomora Book-and-Sprout Symbol (+5 more)

### Community 12 - "View"
Cohesion: 0.09
Nodes (24): CaseIterable, ColorScheme, Mode, UInt, AccentChoice, forest, gold, teal (+16 more)

### Community 13 - "TodayView"
Cohesion: 0.13
Nodes (11): TodayViewModel, TodayView, State, error, idle, loaded, loading, String (+3 more)

### Community 14 - "URLProtocolStub"
Cohesion: 0.16
Nodes (10): FeedView, FeedKind, discover, following, FeedViewModel, State, error, idle (+2 more)

### Community 17 - "OnboardingView.swift"
Cohesion: 0.06
Nodes (42): Content, PreviewProvider, SwiftUI, UIKeyboardType, FeatureNavigation, MainTabView, QuickReadView, RootView (+34 more)

## Knowledge Gaps
- **78 isolated node(s):** `book`, `libraryBook`, `reading`, `sessionSummary`, `post` (+73 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **69 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `APIClientProtocol` connect `APIClientProtocol` to `Equatable`, `APIError`, `SearchViewModel`, `AppContainer`, `ReadingStatus`, `TodayView`, `URLProtocolStub`, `OnboardingView.swift`?**
  _High betweenness centrality (0.094) - this node is a cross-community bridge._
- **Why does `Endpoint` connect `AppContainer` to `Equatable`, `APIClientProtocol`, `APIError`, `SearchViewModel`, `TokenPair`, `TodayView`, `URLProtocolStub`?**
  _High betweenness centrality (0.076) - this node is a cross-community bridge._
- **Why does `AppRoute` connect `AppRoute` to `Equatable`, `OnboardingView.swift`, `ReadingTimer`?**
  _High betweenness centrality (0.044) - this node is a cross-community bridge._
- **Are the 24 inferred relationships involving `Endpoint` (e.g. with `login()` and `refreshUser()`) actually correct?**
  _`Endpoint` has 24 INFERRED edges - model-reasoned connections that need verification._
- **Are the 2 inferred relationships involving `APIClientProtocol` (e.g. with `.search()` and `load()`) actually correct?**
  _`APIClientProtocol` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `book`, `libraryBook`, `reading` to the rest of the system?**
  _78 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Equatable` be split into smaller, more focused modules?**
  _Cohesion score 0.13588850174216027 - nodes in this community are weakly interconnected._
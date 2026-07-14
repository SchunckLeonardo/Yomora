# Graph Report - ios  (2026-07-14)

## Corpus Check
- 47 files · ~59,967 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 457 nodes · 809 edges · 84 communities (17 shown, 67 thin omitted)
- Extraction: 89% EXTRACTED · 11% INFERRED · 0% AMBIGUOUS · INFERRED: 85 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `dcd82d3a`
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

## Communities (84 total, 67 thin omitted)

### Community 0 - "Equatable"
Cohesion: 0.12
Nodes (41): CaseIterable, Codable, Equatable, Hashable, Identifiable, Sendable, String, Book (+33 more)

### Community 1 - "APIClientProtocol"
Cohesion: 0.10
Nodes (17): Foundation, Observation, NoopReadingActivityManager, ReadingActivityManaging, LibraryView, LibraryViewModel, State, error (+9 more)

### Community 2 - "APIError"
Cohesion: 0.12
Nodes (15): Decodable, LocalizedError, APIError, invalidResponse, invalidURL, server, unauthorized, Method (+7 more)

### Community 3 - "SearchViewModel"
Cohesion: 0.11
Nodes (15): ModelContext, init(), APIClientProtocol, BookCache, CachedBook, ProfileEditView, SearchView, SearchViewModel (+7 more)

### Community 4 - "AppContainer"
Cohesion: 0.17
Nodes (13): URLQueryItem, Body, login(), refreshUser(), register(), requestPasswordReset(), restore(), State (+5 more)

### Community 5 - "ReadingTimer"
Cohesion: 0.08
Nodes (16): HTTPURLResponse, TodayViewModel, URLProtocol, APIClientTests, URLProtocolStub, ReadingTimerTests, SearchViewModelTests, SessionStoreTests (+8 more)

### Community 6 - "TokenPair"
Cohesion: 0.16
Nodes (9): Any, Error, OSStatus, Security, InMemoryTokenStore, KeychainError, KeychainTokenStore, TokenStoring (+1 more)

### Community 7 - "StubAPIClient"
Cohesion: 0.24
Nodes (4): Encoder, AnyEncodable, Encodable, StubAPIClient

### Community 8 - "AppRoute"
Cohesion: 0.18
Nodes (10): AppRoute, book, composer, followers, libraryBook, post, profile, reading (+2 more)

### Community 9 - "ReadingStatus"
Cohesion: 0.11
Nodes (11): ReadingTimer, ReadingSessionView, ReadingSessionViewModel, State, error, finished, finishing, idle (+3 more)

### Community 10 - "APIClient"
Cohesion: 0.33
Nodes (3): URL, URLSession, APIClient

### Community 11 - "Yomora Application Target"
Cohesion: 0.19
Nodes (13): iOS 17 Deployment Target, Local API Endpoint at Port 8080, Swift 6 Configuration, Yomora Application Target, Yomora iOS Project, Yomora UI Test Target, Yomora Unit Test Target, Yomora Book-and-Sprout Symbol (+5 more)

### Community 12 - "View"
Cohesion: 0.12
Nodes (17): ColorScheme, UInt, AccentChoice, forest, gold, teal, terracotta, AppTheme (+9 more)

### Community 13 - "TodayView"
Cohesion: 0.10
Nodes (13): App, Scene, SwiftData, AppContainer, YomoraApp, UITestAPIClient, TodayView, State (+5 more)

### Community 14 - "URLProtocolStub"
Cohesion: 0.20
Nodes (7): FeedView, FeedViewModel, State, error, idle, loaded, loading

### Community 17 - "OnboardingView.swift"
Cohesion: 0.05
Nodes (44): Content, PreviewProvider, SwiftUI, UIKeyboardType, FeatureNavigation, MainTabView, QuickReadView, RootView (+36 more)

### Community 76 - "SessionStore"
Cohesion: 0.32
Nodes (6): Mode, AuthenticationView, Mode, login, register, PasswordResetView

## Knowledge Gaps
- **79 isolated node(s):** `book`, `libraryBook`, `reading`, `sessionSummary`, `post` (+74 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **67 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `APIClientProtocol` connect `SearchViewModel` to `Equatable`, `APIClientProtocol`, `AppContainer`, `ReadingTimer`, `StubAPIClient`, `ReadingStatus`, `APIClient`, `TodayView`, `URLProtocolStub`, `OnboardingView.swift`?**
  _High betweenness centrality (0.092) - this node is a cross-community bridge._
- **Why does `Endpoint` connect `AppContainer` to `Equatable`, `APIClientProtocol`, `APIError`, `SearchViewModel`, `ReadingTimer`, `TokenPair`, `URLProtocolStub`, `OnboardingView.swift`?**
  _High betweenness centrality (0.076) - this node is a cross-community bridge._
- **Why does `AppRoute` connect `AppRoute` to `Equatable`, `OnboardingView.swift`, `ReadingTimer`?**
  _High betweenness centrality (0.044) - this node is a cross-community bridge._
- **Are the 24 inferred relationships involving `Endpoint` (e.g. with `login()` and `refreshUser()`) actually correct?**
  _`Endpoint` has 24 INFERRED edges - model-reasoned connections that need verification._
- **Are the 2 inferred relationships involving `APIClientProtocol` (e.g. with `.search()` and `load()`) actually correct?**
  _`APIClientProtocol` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `book`, `libraryBook`, `reading` to the rest of the system?**
  _79 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Equatable` be split into smaller, more focused modules?**
  _Cohesion score 0.11980676328502415 - nodes in this community are weakly interconnected._
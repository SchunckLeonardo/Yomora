# Graph Report - ios  (2026-07-14)

## Corpus Check
- 48 files · ~61,843 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 517 nodes · 1050 edges · 49 communities (14 shown, 35 thin omitted)
- Extraction: 90% EXTRACTED · 10% INFERRED · 0% AMBIGUOUS · INFERRED: 110 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `5cf660f8`
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
- ButtonsAndFields.swift
- SettingsView
- StatisticsView
- Void
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
- String
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
1. `APIClientProtocol` - 40 edges
2. `Endpoint` - 40 edges
3. `Post` - 26 edges
4. `AppContainer` - 25 edges
5. `SwiftUI` - 20 edges
6. `Book` - 18 edges
7. `Foundation` - 17 edges
8. `Body` - 17 edges
9. `LibraryBook` - 17 edges
10. `TokenPair` - 16 edges

## Surprising Connections (you probably didn't know these)
- `Yomora Application Target` --references--> `Yomora App Icon Artwork`  [INFERRED]
  project.yml → Yomora/Resources/Assets.xcassets/AppIcon.appiconset/app-icon.png
- `Yomora Application Target` --references--> `Yomora Logo Artwork`  [INFERRED]
  project.yml → Yomora/Resources/Assets.xcassets/Logo.imageset/logo.png
- `Body` --implements--> `Encodable`  [EXTRACTED]
  Yomora/App/SessionStore.swift → YomoraTests/TestSupport.swift
- `TodayAPIClient` --implements--> `APIClientProtocol`  [EXTRACTED]
  YomoraTests/TodayViewModelTests.swift → Yomora/Core/Networking/APIClient.swift
- `FeedEngagementAPI` --implements--> `APIClientProtocol`  [EXTRACTED]
  YomoraTests/TokenAndNavigationTests.swift → Yomora/Core/Networking/APIClient.swift

## Import Cycles
- None detected.

## Communities (49 total, 35 thin omitted)

### Community 0 - "Equatable"
Cohesion: 0.12
Nodes (45): Codable, Equatable, Hashable, Identifiable, Sendable, QuickReadView, Book, Comment (+37 more)

### Community 1 - "APIClientProtocol"
Cohesion: 0.05
Nodes (33): Content, SwiftData, SwiftUI, AppContainer, State, restoring, signedIn, signedOut (+25 more)

### Community 2 - "APIError"
Cohesion: 0.10
Nodes (16): Set, FeedView, FeedKind, discover, following, FeedViewModel, State, error (+8 more)

### Community 3 - "SearchViewModel"
Cohesion: 0.07
Nodes (22): ModelContext, Observation, URLQueryItem, BookCache, CachedBook, State, error, finished (+14 more)

### Community 4 - "AppContainer"
Cohesion: 0.09
Nodes (23): GeometryProxy, Notification, PostType, String, Body, init(), login(), refreshUser() (+15 more)

### Community 5 - "ReadingTimer"
Cohesion: 0.06
Nodes (22): HTTPURLResponse, TodayViewModel, UIKit, URLProtocol, APIClientTests, URLProtocolStub, ReadingTimerTests, SessionStoreTests (+14 more)

### Community 6 - "TokenPair"
Cohesion: 0.11
Nodes (15): Any, App, Error, OSStatus, Scene, Security, URL, URLSession (+7 more)

### Community 7 - "StubAPIClient"
Cohesion: 0.06
Nodes (44): ButtonStyle, Configuration, PreviewProvider, UIKeyboardType, View, BookCard, BookCover, ProgressRing (+36 more)

### Community 8 - "AppRoute"
Cohesion: 0.07
Nodes (18): Encoder, Foundation, AppRoute, book, composer, followers, libraryBook, post (+10 more)

### Community 9 - "ReadingStatus"
Cohesion: 0.36
Nodes (7): BookWritingView, LibraryBookDetailsView, Mode, note, review, ShelfListView, Int

### Community 10 - "APIClient"
Cohesion: 0.13
Nodes (14): Decodable, LocalizedError, APIError, invalidResponse, invalidURL, server, unauthorized, Method (+6 more)

### Community 11 - "Yomora Application Target"
Cohesion: 0.19
Nodes (13): iOS 17 Deployment Target, Local API Endpoint at Port 8080, Swift 6 Configuration, Yomora Application Target, Yomora iOS Project, Yomora UI Test Target, Yomora Unit Test Target, Yomora Book-and-Sprout Symbol (+5 more)

### Community 12 - "View"
Cohesion: 0.07
Nodes (32): CaseIterable, ColorScheme, Mode, UInt, UIUserInterfaceStyle, AccentChoice, forest, gold (+24 more)

### Community 13 - "TodayView"
Cohesion: 0.15
Nodes (13): applyPersistedGoal(), init(), isCancellation(), load(), State, error, idle, loaded (+5 more)

## Knowledge Gaps
- **78 isolated node(s):** `book`, `libraryBook`, `reading`, `sessionSummary`, `post` (+73 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **35 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `APIClientProtocol` connect `AppContainer` to `Equatable`, `APIClientProtocol`, `APIError`, `SearchViewModel`, `ReadingTimer`, `TokenPair`, `StubAPIClient`, `AppRoute`, `ReadingStatus`, `TodayView`?**
  _High betweenness centrality (0.121) - this node is a cross-community bridge._
- **Why does `Endpoint` connect `AppContainer` to `Equatable`, `APIClientProtocol`, `APIError`, `SearchViewModel`, `ReadingTimer`, `TokenPair`, `APIClient`, `TodayView`?**
  _High betweenness centrality (0.085) - this node is a cross-community bridge._
- **Why does `Post` connect `Equatable` to `APIError`, `AppContainer`, `ReadingTimer`, `StubAPIClient`?**
  _High betweenness centrality (0.062) - this node is a cross-community bridge._
- **Are the 11 inferred relationships involving `APIClientProtocol` (e.g. with `.add()` and `.comment()`) actually correct?**
  _`APIClientProtocol` has 11 INFERRED edges - model-reasoned connections that need verification._
- **Are the 25 inferred relationships involving `Endpoint` (e.g. with `login()` and `refreshUser()`) actually correct?**
  _`Endpoint` has 25 INFERRED edges - model-reasoned connections that need verification._
- **Are the 4 inferred relationships involving `AppContainer` (e.g. with `.save()` and `.follow()`) actually correct?**
  _`AppContainer` has 4 INFERRED edges - model-reasoned connections that need verification._
- **What connects `book`, `libraryBook`, `reading` to the rest of the system?**
  _78 weakly-connected nodes found - possible documentation gaps or missing edges._
# Graph Report - .  (2026-07-13)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 444 nodes · 978 edges · 24 communities (22 shown, 2 thin omitted)
- Extraction: 88% EXTRACTED · 12% INFERRED · 0% AMBIGUOUS · INFERRED: 113 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `495ba23e`
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
- FeedViewModel
- AppRoute
- ReadingStatus
- StubAPIClient
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

## God Nodes (most connected - your core abstractions)
1. `APIClientProtocol` - 42 edges
2. `Endpoint` - 40 edges
3. `AppContainer` - 29 edges
4. `Book` - 21 edges
5. `LibraryBook` - 21 edges
6. `AppRoute` - 19 edges
7. `SwiftUI` - 19 edges
8. `Foundation` - 17 edges
9. `Body` - 17 edges
10. `Post` - 17 edges

## Surprising Connections (you probably didn't know these)
- `Yomora Application Target` --references--> `Yomora App Icon Artwork`  [INFERRED]
  project.yml → Yomora/Resources/Assets.xcassets/AppIcon.appiconset/app-icon.png
- `Yomora Application Target` --references--> `Yomora Logo Artwork`  [INFERRED]
  project.yml → Yomora/Resources/Assets.xcassets/Logo.imageset/logo.png
- `Body` --implements--> `Encodable`  [EXTRACTED]
  Yomora/App/SessionStore.swift → YomoraTests/TestSupport.swift
- `StubAPIClient` --implements--> `APIClientProtocol`  [EXTRACTED]
  YomoraTests/TestSupport.swift → Yomora/Core/Networking/APIClient.swift
- `init()` --references--> `APIClientProtocol`  [EXTRACTED]
  Yomora/Features/Today/TodayViewModel.swift → Yomora/Core/Networking/APIClient.swift

## Import Cycles
- None detected.

## Communities (24 total, 2 thin omitted)

### Community 0 - "Equatable"
Cohesion: 0.11
Nodes (49): Codable, Equatable, Hashable, Identifiable, Sendable, QuickReadView, Book, Comment (+41 more)

### Community 1 - "APIClientProtocol"
Cohesion: 0.10
Nodes (27): URL, URLQueryItem, URLSession, Body, init(), login(), refreshUser(), register() (+19 more)

### Community 2 - "APIError"
Cohesion: 0.06
Nodes (38): ColorScheme, Decodable, LocalizedError, String, UInt, AccentChoice, forest, gold (+30 more)

### Community 3 - "SearchViewModel"
Cohesion: 0.09
Nodes (19): APIClientProtocol, Book, BookCache, ModelContext, SwiftData, BookCache, CachedBook, Data (+11 more)

### Community 4 - "AppContainer"
Cohesion: 0.09
Nodes (20): AppContainer, ModelContainer, RootView, SessionStore, State, restoring, signedIn, signedOut (+12 more)

### Community 5 - "ReadingTimer"
Cohesion: 0.13
Nodes (13): XCTest, XCTestCase, Yomora, ReadingTimer, Bool, Date, TimeInterval, APIClientTests (+5 more)

### Community 6 - "TokenPair"
Cohesion: 0.14
Nodes (12): Any, App, Error, OSStatus, Scene, Security, YomoraApp, InMemoryTokenStore (+4 more)

### Community 7 - "FeedViewModel"
Cohesion: 0.11
Nodes (19): CaseIterable, Mode, AuthenticationView, Mode, login, register, PasswordResetView, SessionStore (+11 more)

### Community 8 - "AppRoute"
Cohesion: 0.10
Nodes (15): Foundation, Observation, AppRoute, book, composer, followers, libraryBook, post (+7 more)

### Community 9 - "ReadingStatus"
Cohesion: 0.12
Nodes (17): ReadingStatus, abandoned, finished, paused, reading, wantToRead, BookWritingView, LibraryBookDetailsView (+9 more)

### Community 10 - "StubAPIClient"
Cohesion: 0.18
Nodes (8): Encoder, AnyEncodable, Void, Encodable, StubAPIClient, Data, String, T

### Community 11 - "Yomora Application Target"
Cohesion: 0.19
Nodes (13): iOS 17 Deployment Target, Local API Endpoint at Port 8080, Swift 6 Configuration, Yomora Application Target, Yomora iOS Project, Yomora UI Test Target, Yomora Unit Test Target, Yomora Book-and-Sprout Symbol (+5 more)

### Community 12 - "View"
Cohesion: 0.27
Nodes (12): View, EmptyStateView, ErrorStateView, LoadingSkeleton, PostCard, ReadingGoalCard, CGFloat, Int (+4 more)

### Community 13 - "TodayView"
Cohesion: 0.20
Nodes (8): TodayViewModel, TodayView, State, error, idle, loaded, loading, String

### Community 14 - "URLProtocolStub"
Cohesion: 0.24
Nodes (6): HTTPURLResponse, URLProtocol, Bool, Data, URLRequest, URLProtocolStub

### Community 15 - "SwiftUI"
Cohesion: 0.22
Nodes (6): SwiftUI, SplashView, BookDetailsView, String, PostComposerView, String

### Community 16 - "BookComponents.swift"
Cohesion: 0.28
Nodes (8): BookCard, BookCover, ProgressRing, RatingView, CGFloat, Double, Int, String

### Community 17 - "OnboardingView.swift"
Cohesion: 0.29
Nodes (6): PreviewProvider, Void, OnboardingPage, OnboardingView, OnboardingView_Previews, String

### Community 18 - "ButtonsAndFields.swift"
Cohesion: 0.43
Nodes (6): UIKeyboardType, PrimaryButton, SecondaryButton, String, Void, YomoraTextField

### Community 19 - "ReadingSessionView"
Cohesion: 0.43
Nodes (4): ReadingSessionView, SessionSummaryView, String, TimeInterval

### Community 20 - "FeatureNavigation"
Cohesion: 0.50
Nodes (4): Content, FeatureNavigation, MainTabView, SessionStore

### Community 21 - "SettingsView"
Cohesion: 0.50
Nodes (3): SettingsView, SessionStore, ThemeSelectionView

## Knowledge Gaps
- **76 isolated node(s):** `book`, `libraryBook`, `reading`, `sessionSummary`, `post` (+71 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **2 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `APIClientProtocol` connect `APIClientProtocol` to `Equatable`, `AppContainer`, `FeedViewModel`, `AppRoute`, `ReadingStatus`, `StubAPIClient`, `SwiftUI`, `StatisticsView`?**
  _High betweenness centrality (0.112) - this node is a cross-community bridge._
- **Why does `Endpoint` connect `APIClientProtocol` to `Equatable`, `APIError`, `StubAPIClient`, `AppContainer`?**
  _High betweenness centrality (0.104) - this node is a cross-community bridge._
- **Why does `AppContainer` connect `AppContainer` to `Equatable`, `APIClientProtocol`, `SearchViewModel`, `TokenPair`, `FeedViewModel`, `ReadingStatus`, `TodayView`, `FeatureNavigation`, `SettingsView`?**
  _High betweenness centrality (0.092) - this node is a cross-community bridge._
- **Are the 18 inferred relationships involving `APIClientProtocol` (e.g. with `login()` and `refreshUser()`) actually correct?**
  _`APIClientProtocol` has 18 INFERRED edges - model-reasoned connections that need verification._
- **Are the 23 inferred relationships involving `Endpoint` (e.g. with `login()` and `refreshUser()`) actually correct?**
  _`Endpoint` has 23 INFERRED edges - model-reasoned connections that need verification._
- **Are the 4 inferred relationships involving `AppContainer` (e.g. with `.save()` and `.follow()`) actually correct?**
  _`AppContainer` has 4 INFERRED edges - model-reasoned connections that need verification._
- **What connects `book`, `libraryBook`, `reading` to the rest of the system?**
  _76 weakly-connected nodes found - possible documentation gaps or missing edges._
# Graph Report - ios  (2026-07-14)

## Corpus Check
- 47 files · ~61,772 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 513 nodes · 1046 edges · 56 communities (22 shown, 34 thin omitted)
- Extraction: 89% EXTRACTED · 11% INFERRED · 0% AMBIGUOUS · INFERRED: 110 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `6b490208`
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
- ReadingSessionView
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
- `StubAPIClient` --implements--> `APIClientProtocol`  [EXTRACTED]
  YomoraTests/TestSupport.swift → Yomora/Core/Networking/APIClient.swift
- `TodayAPIClient` --implements--> `APIClientProtocol`  [EXTRACTED]
  YomoraTests/TodayViewModelTests.swift → Yomora/Core/Networking/APIClient.swift

## Import Cycles
- None detected.

## Communities (56 total, 34 thin omitted)

### Community 0 - "Equatable"
Cohesion: 0.07
Nodes (59): Codable, Decodable, Equatable, Hashable, Identifiable, Sendable, State, restoring (+51 more)

### Community 1 - "APIClientProtocol"
Cohesion: 0.18
Nodes (9): AppContainer, RootView, SessionStore, FollowersView, ProfileView, Int, SessionStore, String (+1 more)

### Community 2 - "APIError"
Cohesion: 0.11
Nodes (19): Set, Post, FeedView, FeedKind, discover, following, FeedViewModel, State (+11 more)

### Community 3 - "SearchViewModel"
Cohesion: 0.13
Nodes (12): ModelContext, URLQueryItem, BookCache, CachedBook, SearchView, SearchViewModel, State, error (+4 more)

### Community 4 - "AppContainer"
Cohesion: 0.11
Nodes (20): GeometryProxy, Notification, PostType, String, Body, init(), login(), refreshUser() (+12 more)

### Community 5 - "ReadingTimer"
Cohesion: 0.06
Nodes (21): HTTPURLResponse, UIKit, UIUserInterfaceStyle, URLProtocol, APIClientTests, URLProtocolStub, ReadingTimerTests, SearchViewModelTests (+13 more)

### Community 6 - "TokenPair"
Cohesion: 0.11
Nodes (15): Any, App, Error, OSStatus, Scene, Security, URL, URLSession (+7 more)

### Community 7 - "StubAPIClient"
Cohesion: 0.18
Nodes (15): View, CommentCard, EmptyStateView, ErrorStateView, LoadingSkeleton, PostCard, PostType, ReadingGoalCard (+7 more)

### Community 8 - "AppRoute"
Cohesion: 0.13
Nodes (15): Content, AppRoute, book, composer, followers, libraryBook, post, profile (+7 more)

### Community 9 - "ReadingStatus"
Cohesion: 0.16
Nodes (12): State, error, finished, finishing, idle, paused, running, starting (+4 more)

### Community 10 - "APIClient"
Cohesion: 0.15
Nodes (12): LocalizedError, APIError, invalidResponse, invalidURL, server, unauthorized, Method, delete (+4 more)

### Community 11 - "Yomora Application Target"
Cohesion: 0.19
Nodes (13): iOS 17 Deployment Target, Local API Endpoint at Port 8080, Swift 6 Configuration, Yomora Application Target, Yomora iOS Project, Yomora UI Test Target, Yomora Unit Test Target, Yomora Book-and-Sprout Symbol (+5 more)

### Community 12 - "View"
Cohesion: 0.08
Nodes (29): CaseIterable, ColorScheme, Mode, UInt, ThemePicker, AccentChoice, forest, gold (+21 more)

### Community 13 - "TodayView"
Cohesion: 0.08
Nodes (18): Foundation, Observation, NoopReadingActivityManager, ReadingActivityManaging, ReadingTimer, applyPersistedGoal(), init(), isCancellation() (+10 more)

### Community 14 - "URLProtocolStub"
Cohesion: 0.21
Nodes (5): TodayViewModel, TodayView, Bool, TodayAPIClient, TodayViewModelTests

### Community 17 - "OnboardingView.swift"
Cohesion: 0.29
Nodes (6): PreviewProvider, OnboardingPage, OnboardingView, OnboardingView_Previews, String, Void

### Community 19 - "ReadingSessionView"
Cohesion: 0.28
Nodes (8): BookCard, BookCover, ProgressRing, RatingView, CGFloat, Double, Int, String

### Community 20 - "FeatureNavigation"
Cohesion: 0.43
Nodes (6): UIKeyboardType, PrimaryButton, SecondaryButton, String, Void, YomoraTextField

### Community 24 - "CGFloat"
Cohesion: 0.22
Nodes (8): LibraryItem, LibraryViewModel, State, error, idle, loaded, loading, LibraryView

### Community 25 - "Double"
Cohesion: 0.18
Nodes (7): SwiftData, SwiftUI, SplashView, Notification.Name, SettingsView, SessionStore, ThemeSelectionView

### Community 26 - "Int"
Cohesion: 0.50
Nodes (3): ButtonStyle, Configuration, SocialActionButtonStyle

### Community 27 - "ReadingSessionView"
Cohesion: 0.38
Nodes (4): Encoder, Encodable, AnyEncodable, Void

## Knowledge Gaps
- **77 isolated node(s):** `book`, `libraryBook`, `reading`, `sessionSummary`, `post` (+72 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **34 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `APIClientProtocol` connect `AppContainer` to `Equatable`, `APIClientProtocol`, `APIError`, `SearchViewModel`, `ReadingTimer`, `TokenPair`, `TodayView`, `URLProtocolStub`, `CGFloat`?**
  _High betweenness centrality (0.122) - this node is a cross-community bridge._
- **Why does `Endpoint` connect `AppContainer` to `Equatable`, `APIClientProtocol`, `APIError`, `SearchViewModel`, `TokenPair`, `APIClient`, `TodayView`, `URLProtocolStub`, `CGFloat`?**
  _High betweenness centrality (0.085) - this node is a cross-community bridge._
- **Why does `Post` connect `APIError` to `Equatable`, `AppContainer`, `StubAPIClient`?**
  _High betweenness centrality (0.062) - this node is a cross-community bridge._
- **Are the 11 inferred relationships involving `APIClientProtocol` (e.g. with `.add()` and `.comment()`) actually correct?**
  _`APIClientProtocol` has 11 INFERRED edges - model-reasoned connections that need verification._
- **Are the 25 inferred relationships involving `Endpoint` (e.g. with `login()` and `refreshUser()`) actually correct?**
  _`Endpoint` has 25 INFERRED edges - model-reasoned connections that need verification._
- **Are the 4 inferred relationships involving `AppContainer` (e.g. with `.save()` and `.follow()`) actually correct?**
  _`AppContainer` has 4 INFERRED edges - model-reasoned connections that need verification._
- **What connects `book`, `libraryBook`, `reading` to the rest of the system?**
  _77 weakly-connected nodes found - possible documentation gaps or missing edges._
# Graph Report - ios  (2026-07-14)

## Corpus Check
- 47 files · ~61,459 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 502 nodes · 1018 edges · 58 communities (22 shown, 36 thin omitted)
- Extraction: 89% EXTRACTED · 11% INFERRED · 0% AMBIGUOUS · INFERRED: 110 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `483c207c`
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
2. `Endpoint` - 38 edges
3. `AppContainer` - 25 edges
4. `Post` - 25 edges
5. `SwiftUI` - 20 edges
6. `Foundation` - 17 edges
7. `Body` - 17 edges
8. `Book` - 17 edges
9. `TokenPair` - 16 edges
10. `UserProfile` - 16 edges

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

## Communities (58 total, 36 thin omitted)

### Community 0 - "Equatable"
Cohesion: 0.12
Nodes (44): Codable, Equatable, Hashable, Identifiable, Sendable, LibraryItem, QuickReadView, Book (+36 more)

### Community 1 - "APIClientProtocol"
Cohesion: 0.06
Nodes (36): AppContainer, init(), State, restoring, signedIn, signedOut, APIClientProtocol, LibraryViewModel (+28 more)

### Community 2 - "APIError"
Cohesion: 0.06
Nodes (26): CaseIterable, Mode, Set, AuthenticationView, Mode, login, register, PasswordResetView (+18 more)

### Community 3 - "SearchViewModel"
Cohesion: 0.08
Nodes (16): Foundation, ModelContext, Observation, SwiftData, BookCache, CachedBook, NoopReadingActivityManager, ReadingActivityManaging (+8 more)

### Community 4 - "AppContainer"
Cohesion: 0.11
Nodes (20): Decodable, PostType, String, URLQueryItem, Body, login(), refreshUser(), register() (+12 more)

### Community 5 - "ReadingTimer"
Cohesion: 0.07
Nodes (18): HTTPURLResponse, UIKit, UIUserInterfaceStyle, URLProtocol, ReadingTimer, APIClientTests, URLProtocolStub, ReadingTimerTests (+10 more)

### Community 6 - "TokenPair"
Cohesion: 0.10
Nodes (15): Any, App, Error, OSStatus, Scene, Security, URL, URLSession (+7 more)

### Community 7 - "StubAPIClient"
Cohesion: 0.20
Nodes (15): View, CommentCard, EmptyStateView, ErrorStateView, LoadingSkeleton, PostCard, PostType, ReadingGoalCard (+7 more)

### Community 8 - "AppRoute"
Cohesion: 0.18
Nodes (10): AppRoute, book, composer, followers, libraryBook, post, profile, reading (+2 more)

### Community 9 - "ReadingStatus"
Cohesion: 0.25
Nodes (8): State, error, finished, finishing, idle, paused, running, starting

### Community 10 - "APIClient"
Cohesion: 0.12
Nodes (11): Encoder, LocalizedError, APIError, invalidResponse, invalidURL, server, unauthorized, AnyEncodable (+3 more)

### Community 11 - "Yomora Application Target"
Cohesion: 0.19
Nodes (13): iOS 17 Deployment Target, Local API Endpoint at Port 8080, Swift 6 Configuration, Yomora Application Target, Yomora iOS Project, Yomora UI Test Target, Yomora Unit Test Target, Yomora Book-and-Sprout Symbol (+5 more)

### Community 12 - "View"
Cohesion: 0.12
Nodes (20): ColorScheme, UInt, AccentChoice, forest, gold, teal, terracotta, AppTheme (+12 more)

### Community 13 - "TodayView"
Cohesion: 0.09
Nodes (18): TodayViewModel, TodayView, applyPersistedGoal(), init(), isCancellation(), load(), State, error (+10 more)

### Community 14 - "URLProtocolStub"
Cohesion: 0.29
Nodes (4): SwiftUI, SplashView, StatisticsView, String

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
Cohesion: 0.50
Nodes (4): Content, FeatureNavigation, MainTabView, SessionStore

### Community 25 - "Double"
Cohesion: 0.40
Nodes (4): Notification.Name, SettingsView, SessionStore, ThemeSelectionView

### Community 26 - "Int"
Cohesion: 0.50
Nodes (3): ButtonStyle, Configuration, SocialActionButtonStyle

### Community 27 - "ReadingSessionView"
Cohesion: 0.39
Nodes (4): ReadingSessionView, SessionSummaryView, String, TimeInterval

## Knowledge Gaps
- **77 isolated node(s):** `book`, `libraryBook`, `reading`, `sessionSummary`, `post` (+72 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **36 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `APIClientProtocol` connect `APIClientProtocol` to `Equatable`, `APIError`, `SearchViewModel`, `AppContainer`, `TokenPair`, `APIClient`, `TodayView`, `URLProtocolStub`?**
  _High betweenness centrality (0.126) - this node is a cross-community bridge._
- **Why does `Endpoint` connect `AppContainer` to `Equatable`, `APIClientProtocol`, `APIError`, `SearchViewModel`, `TokenPair`, `TodayView`?**
  _High betweenness centrality (0.079) - this node is a cross-community bridge._
- **Why does `Post` connect `Equatable` to `APIClientProtocol`, `APIError`, `StubAPIClient`?**
  _High betweenness centrality (0.057) - this node is a cross-community bridge._
- **Are the 11 inferred relationships involving `APIClientProtocol` (e.g. with `.add()` and `.comment()`) actually correct?**
  _`APIClientProtocol` has 11 INFERRED edges - model-reasoned connections that need verification._
- **Are the 25 inferred relationships involving `Endpoint` (e.g. with `login()` and `refreshUser()`) actually correct?**
  _`Endpoint` has 25 INFERRED edges - model-reasoned connections that need verification._
- **Are the 4 inferred relationships involving `AppContainer` (e.g. with `.save()` and `.follow()`) actually correct?**
  _`AppContainer` has 4 INFERRED edges - model-reasoned connections that need verification._
- **What connects `book`, `libraryBook`, `reading` to the rest of the system?**
  _77 weakly-connected nodes found - possible documentation gaps or missing edges._
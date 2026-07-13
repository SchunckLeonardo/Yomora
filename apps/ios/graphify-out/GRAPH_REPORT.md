# Graph Report - .  (2026-07-13)

## Corpus Check
- 49 files · ~59,514 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 440 nodes · 983 edges · 26 communities (25 shown, 1 thin omitted)
- Extraction: 88% EXTRACTED · 12% INFERRED · 0% AMBIGUOUS · INFERRED: 118 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Session and Networking
- API Models
- Navigation Tests
- Theme Model
- App Dependency Container
- Endpoint Definitions
- Search Cache
- Reading Activity
- Keychain Security
- Profile Feature
- UI Test Client
- Feed Feature
- Xcode Project Assets
- State Components
- App Root
- Network Unit Tests
- Post Types
- Book Components
- Reading Timer
- Reading Session UI
- Onboarding
- Form Components
- Root Session Flow
- Main Tab Navigation
- Today Feature
- Settings Feature

## God Nodes (most connected - your core abstractions)
1. `APIClientProtocol` - 45 edges
2. `Endpoint` - 41 edges
3. `AppContainer` - 29 edges
4. `Book` - 22 edges
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

## Communities (26 total, 1 thin omitted)

### Community 0 - "Session and Networking"
Cohesion: 0.11
Nodes (25): URL, URLSession, Body, init(), login(), refreshUser(), register(), requestPasswordReset() (+17 more)

### Community 1 - "API Models"
Cohesion: 0.16
Nodes (37): Codable, Equatable, Hashable, Identifiable, Sendable, QuickReadView, Book, Comment (+29 more)

### Community 2 - "Navigation Tests"
Cohesion: 0.08
Nodes (21): XCTest, XCTestCase, Yomora, AppRoute, book, composer, followers, libraryBook (+13 more)

### Community 3 - "Theme Model"
Cohesion: 0.08
Nodes (27): CaseIterable, ColorScheme, Mode, UInt, AccentChoice, forest, gold, teal (+19 more)

### Community 4 - "App Dependency Container"
Cohesion: 0.09
Nodes (20): Foundation, Observation, SwiftData, LibraryView, LibraryItem, LibraryViewModel, State, error (+12 more)

### Community 5 - "Endpoint Definitions"
Cohesion: 0.10
Nodes (22): Decodable, LocalizedError, String, APIError, invalidResponse, invalidURL, server, unauthorized (+14 more)

### Community 6 - "Search Cache"
Cohesion: 0.12
Nodes (16): ModelContext, URLQueryItem, BookCache, CachedBook, Data, Date, ModelContainer, UUID (+8 more)

### Community 7 - "Reading Activity"
Cohesion: 0.11
Nodes (16): NoopReadingActivityManager, ReadingActivityManaging, Date, String, ReadingSessionViewModel, State, error, finished (+8 more)

### Community 8 - "Keychain Security"
Cohesion: 0.18
Nodes (9): Any, Error, OSStatus, Security, InMemoryTokenStore, KeychainError, KeychainTokenStore, String (+1 more)

### Community 9 - "Profile Feature"
Cohesion: 0.18
Nodes (10): AppContainer, ModelContainer, FollowersView, ProfileEditView, ProfileView, Bool, Int, SessionStore (+2 more)

### Community 10 - "UI Test Client"
Cohesion: 0.16
Nodes (9): Encoder, AnyEncodable, Void, UITestAPIClient, Encodable, StubAPIClient, Data, String (+1 more)

### Community 11 - "Feed Feature"
Cohesion: 0.18
Nodes (11): FeedView, FeedKind, discover, following, FeedViewModel, State, error, idle (+3 more)

### Community 12 - "Xcode Project Assets"
Cohesion: 0.19
Nodes (13): iOS 17 Deployment Target, Local API Endpoint at Port 8080, Swift 6 Configuration, Yomora Application Target, Yomora iOS Project, Yomora UI Test Target, Yomora Unit Test Target, Yomora Book-and-Sprout Symbol (+5 more)

### Community 13 - "State Components"
Cohesion: 0.27
Nodes (12): View, EmptyStateView, ErrorStateView, LoadingSkeleton, PostCard, ReadingGoalCard, CGFloat, Int (+4 more)

### Community 14 - "App Root"
Cohesion: 0.18
Nodes (7): App, Scene, SwiftUI, SplashView, YomoraApp, StatisticsView, String

### Community 15 - "Network Unit Tests"
Cohesion: 0.24
Nodes (6): HTTPURLResponse, URLProtocol, Bool, Data, URLRequest, URLProtocolStub

### Community 16 - "Post Types"
Cohesion: 0.24
Nodes (8): PostType, note, progress, quote, recommendation, review, PostComposerView, String

### Community 17 - "Book Components"
Cohesion: 0.28
Nodes (8): BookCard, BookCover, ProgressRing, RatingView, CGFloat, Double, Int, String

### Community 18 - "Reading Timer"
Cohesion: 0.42
Nodes (4): ReadingTimer, Bool, Date, TimeInterval

### Community 19 - "Reading Session UI"
Cohesion: 0.39
Nodes (4): ReadingSessionView, SessionSummaryView, String, TimeInterval

### Community 20 - "Onboarding"
Cohesion: 0.29
Nodes (6): PreviewProvider, OnboardingPage, OnboardingView, OnboardingView_Previews, String, Void

### Community 21 - "Form Components"
Cohesion: 0.43
Nodes (6): UIKeyboardType, PrimaryButton, SecondaryButton, String, Void, YomoraTextField

### Community 22 - "Root Session Flow"
Cohesion: 0.33
Nodes (6): RootView, SessionStore, State, restoring, signedIn, signedOut

### Community 23 - "Main Tab Navigation"
Cohesion: 0.50
Nodes (4): Content, FeatureNavigation, MainTabView, SessionStore

### Community 25 - "Settings Feature"
Cohesion: 0.50
Nodes (3): SettingsView, SessionStore, ThemeSelectionView

## Knowledge Gaps
- **76 isolated node(s):** `book`, `libraryBook`, `reading`, `sessionSummary`, `post` (+71 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **1 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `APIClientProtocol` connect `Session and Networking` to `API Models`, `App Dependency Container`, `Endpoint Definitions`, `Search Cache`, `Reading Activity`, `Profile Feature`, `UI Test Client`, `Feed Feature`, `App Root`, `Post Types`?**
  _High betweenness centrality (0.124) - this node is a cross-community bridge._
- **Why does `Endpoint` connect `Session and Networking` to `API Models`, `App Dependency Container`, `Endpoint Definitions`, `Search Cache`, `Profile Feature`, `UI Test Client`?**
  _High betweenness centrality (0.109) - this node is a cross-community bridge._
- **Why does `AppContainer` connect `Profile Feature` to `Session and Networking`, `API Models`, `App Dependency Container`, `Endpoint Definitions`, `Search Cache`, `Reading Activity`, `Feed Feature`, `Reading Session UI`, `Root Session Flow`, `Main Tab Navigation`, `Today Feature`, `Settings Feature`?**
  _High betweenness centrality (0.088) - this node is a cross-community bridge._
- **Are the 19 inferred relationships involving `APIClientProtocol` (e.g. with `login()` and `refreshUser()`) actually correct?**
  _`APIClientProtocol` has 19 INFERRED edges - model-reasoned connections that need verification._
- **Are the 24 inferred relationships involving `Endpoint` (e.g. with `login()` and `refreshUser()`) actually correct?**
  _`Endpoint` has 24 INFERRED edges - model-reasoned connections that need verification._
- **Are the 4 inferred relationships involving `AppContainer` (e.g. with `.save()` and `.follow()`) actually correct?**
  _`AppContainer` has 4 INFERRED edges - model-reasoned connections that need verification._
- **What connects `book`, `libraryBook`, `reading` to the rest of the system?**
  _76 weakly-connected nodes found - possible documentation gaps or missing edges._
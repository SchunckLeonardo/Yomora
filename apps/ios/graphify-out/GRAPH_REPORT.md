# Graph Report - ios  (2026-07-15)

## Corpus Check
- 49 files · ~62,169 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 533 nodes · 1101 edges · 58 communities (24 shown, 34 thin omitted)
- Extraction: 89% EXTRACTED · 11% INFERRED · 0% AMBIGUOUS · INFERRED: 118 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `df207dd5`
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
- SwiftUI
- SettingsView
- StatisticsView
- Void
- BookComponents.swift
- AnyEncodable
- OnboardingView.swift
- ProfileEditView
- AuthenticationView
- SettingsView
- SocialActionButtonStyle
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
- `StubAPIClient` --implements--> `APIClientProtocol`  [EXTRACTED]
  YomoraTests/TestSupport.swift → Yomora/Core/Networking/APIClient.swift
- `TodayAPIClient` --implements--> `APIClientProtocol`  [EXTRACTED]
  YomoraTests/TodayViewModelTests.swift → Yomora/Core/Networking/APIClient.swift

## Import Cycles
- None detected.

## Communities (58 total, 34 thin omitted)

### Community 0 - "Equatable"
Cohesion: 0.12
Nodes (46): Codable, Equatable, Hashable, Identifiable, Sendable, LibraryItem, QuickReadView, Book (+38 more)

### Community 1 - "APIClientProtocol"
Cohesion: 0.10
Nodes (18): AppContainer, State, restoring, signedIn, signedOut, LibraryViewModel, State, error (+10 more)

### Community 2 - "APIError"
Cohesion: 0.14
Nodes (14): Set, FeedView, FeedKind, discover, following, FeedViewModel, State, error (+6 more)

### Community 3 - "SearchViewModel"
Cohesion: 0.06
Nodes (23): Foundation, ModelContext, Observation, SwiftData, BookCache, CachedBook, NoopReadingActivityManager, ReadingActivityManaging (+15 more)

### Community 4 - "AppContainer"
Cohesion: 0.08
Nodes (25): GeometryProxy, Notification, String, URLQueryItem, Body, init(), login(), refreshUser() (+17 more)

### Community 5 - "ReadingTimer"
Cohesion: 0.06
Nodes (20): UIKit, UIUserInterfaceStyle, APIClientTests, ReadingTimerTests, SearchViewModelTests, SessionStoreTests, StubAPIClient, XCTest (+12 more)

### Community 6 - "TokenPair"
Cohesion: 0.08
Nodes (18): Any, App, Error, HTTPURLResponse, OSStatus, Scene, Security, URL (+10 more)

### Community 7 - "StubAPIClient"
Cohesion: 0.21
Nodes (14): View, CommentCard, EmptyStateView, ErrorStateView, LoadingSkeleton, PostCard, PostType, ReadingGoalCard (+6 more)

### Community 8 - "AppRoute"
Cohesion: 0.14
Nodes (14): Content, AppRoute, book, composer, followers, libraryBook, post, profile (+6 more)

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
Cohesion: 0.10
Nodes (25): CaseIterable, ColorScheme, UInt, ThemePicker, AccentChoice, forest, gold, teal (+17 more)

### Community 13 - "TodayView"
Cohesion: 0.14
Nodes (11): TodayViewModel, TodayView, State, error, idle, loaded, loading, String (+3 more)

### Community 17 - "TextDraft"
Cohesion: 0.22
Nodes (15): Axis, FocusState, UIKeyboardType, UITextContentType, DraftActionButton, DraftTextField, PrimaryButton, SecondaryButton (+7 more)

### Community 19 - "State"
Cohesion: 0.15
Nodes (13): State, error, finished, finishing, idle, paused, running, starting (+5 more)

### Community 20 - "SwiftUI"
Cohesion: 0.20
Nodes (7): PostType, SwiftUI, SplashView, PostComposerView, String, StatisticsView, String

### Community 24 - "BookComponents.swift"
Cohesion: 0.28
Nodes (8): BookCard, BookCover, ProgressRing, RatingView, CGFloat, Double, Int, String

### Community 25 - "AnyEncodable"
Cohesion: 0.38
Nodes (4): Encoder, Encodable, AnyEncodable, Void

### Community 26 - "OnboardingView.swift"
Cohesion: 0.29
Nodes (6): PreviewProvider, OnboardingPage, OnboardingView, OnboardingView_Previews, String, Void

### Community 27 - "ProfileEditView"
Cohesion: 0.33
Nodes (4): FollowersView, ProfileEditView, Bool, UUID

### Community 28 - "AuthenticationView"
Cohesion: 0.50
Nodes (4): Mode, AuthenticationView, PasswordResetView, SessionStore

### Community 29 - "SettingsView"
Cohesion: 0.40
Nodes (4): Notification.Name, SettingsView, SessionStore, ThemeSelectionView

### Community 30 - "SocialActionButtonStyle"
Cohesion: 0.50
Nodes (3): ButtonStyle, Configuration, SocialActionButtonStyle

## Knowledge Gaps
- **78 isolated node(s):** `book`, `libraryBook`, `reading`, `sessionSummary`, `post` (+73 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **34 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `APIClientProtocol` connect `AppContainer` to `Equatable`, `APIClientProtocol`, `APIError`, `SearchViewModel`, `ReadingTimer`, `TokenPair`, `ReadingStatus`, `TodayView`, `SwiftUI`, `ProfileEditView`?**
  _High betweenness centrality (0.123) - this node is a cross-community bridge._
- **Why does `Endpoint` connect `AppContainer` to `Equatable`, `APIClientProtocol`, `TokenPair`, `APIClient`, `TodayView`?**
  _High betweenness centrality (0.081) - this node is a cross-community bridge._
- **Why does `Post` connect `Equatable` to `APIError`, `AppContainer`, `StubAPIClient`?**
  _High betweenness centrality (0.062) - this node is a cross-community bridge._
- **Are the 12 inferred relationships involving `APIClientProtocol` (e.g. with `.add()` and `.comment()`) actually correct?**
  _`APIClientProtocol` has 12 INFERRED edges - model-reasoned connections that need verification._
- **Are the 25 inferred relationships involving `Endpoint` (e.g. with `login()` and `refreshUser()`) actually correct?**
  _`Endpoint` has 25 INFERRED edges - model-reasoned connections that need verification._
- **Are the 4 inferred relationships involving `AppContainer` (e.g. with `.save()` and `.follow()`) actually correct?**
  _`AppContainer` has 4 INFERRED edges - model-reasoned connections that need verification._
- **What connects `book`, `libraryBook`, `reading` to the rest of the system?**
  _78 weakly-connected nodes found - possible documentation gaps or missing edges._
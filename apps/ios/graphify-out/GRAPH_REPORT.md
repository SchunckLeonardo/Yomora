# Graph Report - ios  (2026-07-15)

## Corpus Check
- 52 files · ~62,812 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 562 nodes · 1136 edges · 60 communities (26 shown, 34 thin omitted)
- Extraction: 90% EXTRACTED · 10% INFERRED · 0% AMBIGUOUS · INFERRED: 119 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `f41327b5`
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
- TextDraft
- SettingsView
- StatisticsView
- Void
- SwiftUI
- AnyEncodable
- BookComponents.swift
- LibraryBookDetailsView
- OnboardingView.swift
- Q: github actions ios test is failing
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
- `TodayAPIClient` --implements--> `APIClientProtocol`  [EXTRACTED]
  YomoraTests/TodayViewModelTests.swift → Yomora/Core/Networking/APIClient.swift
- `FeedEngagementAPI` --references--> `Post`  [EXTRACTED]
  YomoraTests/TokenAndNavigationTests.swift → Yomora/Core/Networking/APIModels.swift

## Import Cycles
- None detected.

## Communities (60 total, 34 thin omitted)

### Community 0 - "Equatable"
Cohesion: 0.12
Nodes (45): Codable, Equatable, Hashable, Identifiable, Sendable, QuickReadView, Book, Comment (+37 more)

### Community 1 - "APIClientProtocol"
Cohesion: 0.08
Nodes (22): AppContainer, State, restoring, signedIn, signedOut, LibraryItem, LibraryViewModel, State (+14 more)

### Community 2 - "APIError"
Cohesion: 0.12
Nodes (16): Set, FeedView, FeedKind, discover, following, FeedViewModel, State, error (+8 more)

### Community 3 - "SearchViewModel"
Cohesion: 0.08
Nodes (16): Encoder, Foundation, Observation, NoopReadingActivityManager, ReadingActivityManaging, ReadingTimer, Encodable, AnyEncodable (+8 more)

### Community 4 - "AppContainer"
Cohesion: 0.09
Nodes (23): Decodable, String, Body, init(), login(), refreshUser(), register(), requestPasswordReset() (+15 more)

### Community 5 - "ReadingTimer"
Cohesion: 0.07
Nodes (19): HTTPURLResponse, UIUserInterfaceStyle, URLProtocol, APIClientTests, URLProtocolStub, ReadingTimerTests, SessionStoreTests, XCTest (+11 more)

### Community 6 - "TokenPair"
Cohesion: 0.08
Nodes (21): Any, App, Error, LocalizedError, OSStatus, Scene, Security, URL (+13 more)

### Community 7 - "StubAPIClient"
Cohesion: 0.21
Nodes (14): View, CommentCard, EmptyStateView, ErrorStateView, LoadingSkeleton, PostCard, PostType, ReadingGoalCard (+6 more)

### Community 8 - "AppRoute"
Cohesion: 0.09
Nodes (21): Content, AppRoute, book, composer, followers, libraryBook, post, profile (+13 more)

### Community 9 - "ReadingStatus"
Cohesion: 0.10
Nodes (15): ModelContext, SwiftData, URLQueryItem, BookCache, CachedBook, SearchViewModelTests, SearchBar, SearchView (+7 more)

### Community 10 - "APIClient"
Cohesion: 0.13
Nodes (13): FocusState, GeometryProxy, Notification, UIKit, CommentComposer, KeyboardLayout, PostDetailsView, Bool (+5 more)

### Community 11 - "Yomora Application Target"
Cohesion: 0.19
Nodes (13): iOS 17 Deployment Target, Local API Endpoint at Port 8080, Swift 6 Configuration, Yomora Application Target, Yomora iOS Project, Yomora UI Test Target, Yomora Unit Test Target, Yomora Book-and-Sprout Symbol (+5 more)

### Community 12 - "View"
Cohesion: 0.10
Nodes (25): CaseIterable, ColorScheme, UInt, ThemePicker, AccentChoice, forest, gold, teal (+17 more)

### Community 13 - "TodayView"
Cohesion: 0.13
Nodes (11): TodayViewModel, TodayView, State, error, idle, loaded, loading, String (+3 more)

### Community 17 - "TextDraft"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Quando eu clico para finalizar sessão e aparece o resumo da leitura, e clico na seta para voltar ela volta para o timer em contagem ao invés de voltar para a tela "Hoje", Source Nodes

### Community 19 - "State"
Cohesion: 0.15
Nodes (14): State, error, finished, finishing, idle, paused, running, starting (+6 more)

### Community 20 - "TextDraft"
Cohesion: 0.27
Nodes (13): Axis, UIKeyboardType, UITextContentType, DraftActionButton, DraftTextField, PrimaryButton, SecondaryButton, Bool (+5 more)

### Community 24 - "SwiftUI"
Cohesion: 0.20
Nodes (7): PostType, SwiftUI, SplashView, PostComposerView, String, StatisticsView, String

### Community 25 - "AnyEncodable"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: A parte de escrever uma nota sobre a sessão de leitura está quase invísivel, não da para saber o que está escrito no placeholder, Source Nodes

### Community 26 - "BookComponents.swift"
Cohesion: 0.28
Nodes (8): BookCard, BookCover, ProgressRing, RatingView, CGFloat, Double, Int, String

### Community 27 - "LibraryBookDetailsView"
Cohesion: 0.36
Nodes (7): BookWritingView, LibraryBookDetailsView, Mode, note, review, ShelfListView, Int

### Community 28 - "OnboardingView.swift"
Cohesion: 0.29
Nodes (6): PreviewProvider, OnboardingPage, OnboardingView, OnboardingView_Previews, String, Void

### Community 29 - "Q: github actions ios test is failing"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: github actions ios test is failing, Source Nodes

### Community 30 - "AuthenticationView"
Cohesion: 0.50
Nodes (4): Mode, AuthenticationView, PasswordResetView, SessionStore

### Community 31 - "SettingsView"
Cohesion: 0.40
Nodes (4): Notification.Name, SettingsView, SessionStore, ThemeSelectionView

### Community 32 - "SocialActionButtonStyle"
Cohesion: 0.50
Nodes (3): ButtonStyle, Configuration, SocialActionButtonStyle

## Knowledge Gaps
- **92 isolated node(s):** `book`, `libraryBook`, `reading`, `sessionSummary`, `post` (+87 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **34 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `APIClientProtocol` connect `AppContainer` to `Equatable`, `APIClientProtocol`, `APIError`, `SearchViewModel`, `TokenPair`, `ReadingStatus`, `APIClient`, `TodayView`, `SwiftUI`, `LibraryBookDetailsView`?**
  _High betweenness centrality (0.113) - this node is a cross-community bridge._
- **Why does `Endpoint` connect `AppContainer` to `Equatable`, `APIClientProtocol`, `APIError`, `SearchViewModel`, `TokenPair`, `ReadingStatus`, `APIClient`, `TodayView`?**
  _High betweenness centrality (0.075) - this node is a cross-community bridge._
- **Why does `Post` connect `Equatable` to `APIError`, `APIClient`, `AppContainer`, `StubAPIClient`?**
  _High betweenness centrality (0.060) - this node is a cross-community bridge._
- **Are the 12 inferred relationships involving `APIClientProtocol` (e.g. with `.add()` and `.comment()`) actually correct?**
  _`APIClientProtocol` has 12 INFERRED edges - model-reasoned connections that need verification._
- **Are the 25 inferred relationships involving `Endpoint` (e.g. with `login()` and `refreshUser()`) actually correct?**
  _`Endpoint` has 25 INFERRED edges - model-reasoned connections that need verification._
- **Are the 4 inferred relationships involving `AppContainer` (e.g. with `.save()` and `.follow()`) actually correct?**
  _`AppContainer` has 4 INFERRED edges - model-reasoned connections that need verification._
- **What connects `book`, `libraryBook`, `reading` to the rest of the system?**
  _92 weakly-connected nodes found - possible documentation gaps or missing edges._
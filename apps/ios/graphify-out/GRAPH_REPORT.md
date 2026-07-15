# Graph Report - ios  (2026-07-15)

## Corpus Check
- 53 files · ~64,966 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 643 nodes · 1340 edges · 60 communities (25 shown, 35 thin omitted)
- Extraction: 90% EXTRACTED · 10% INFERRED · 0% AMBIGUOUS · INFERRED: 139 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `6574785f`
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
- LibraryBook
- URL
- Bool
- Data
- T
- URLRequest
- LibraryBook
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
- Bool
- Data
- URLRequest
- Data
- String
- T

## God Nodes (most connected - your core abstractions)
1. `APIClientProtocol` - 49 edges
2. `Endpoint` - 48 edges
3. `AppContainer` - 27 edges
4. `Post` - 26 edges
5. `SwiftUI` - 20 edges
6. `Book` - 20 edges
7. `Foundation` - 19 edges
8. `FeedViewModel` - 19 edges
9. `ReadingSessionViewModel` - 19 edges
10. `TextDraft` - 18 edges

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

## Communities (60 total, 35 thin omitted)

### Community 0 - "Equatable"
Cohesion: 0.07
Nodes (60): Codable, Decodable, Encoder, Equatable, Hashable, Identifiable, LocalizedError, Sendable (+52 more)

### Community 1 - "APIClientProtocol"
Cohesion: 0.09
Nodes (23): AppContainer, LibraryViewModel, State, error, idle, loaded, loading, RootView (+15 more)

### Community 2 - "APIError"
Cohesion: 0.05
Nodes (31): HTTPURLResponse, URLProtocol, APIClientTests, URLProtocolStub, ReadingTimerTests, SessionStoreTests, XCTest, XCTestCase (+23 more)

### Community 3 - "SearchViewModel"
Cohesion: 0.07
Nodes (28): GeometryProxy, Notification, Set, UIKit, Post, FeedView, KeyboardLayout, PostDetailsView (+20 more)

### Community 4 - "AppContainer"
Cohesion: 0.09
Nodes (26): String, Body, init(), login(), refreshUser(), register(), requestPasswordReset(), restore() (+18 more)

### Community 5 - "ReadingTimer"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Melhorar no iOS o cache das capas, a seleção e remoção de leituras pausadas ou abandonadas nas abas Hoje e Ler, e a visibilidade e persistência das notas durante o foco., Source Nodes

### Community 6 - "TokenPair"
Cohesion: 0.11
Nodes (14): Any, App, Error, OSStatus, Scene, Security, URLSession, YomoraApp (+6 more)

### Community 7 - "StubAPIClient"
Cohesion: 0.21
Nodes (14): View, CommentCard, EmptyStateView, ErrorStateView, LoadingSkeleton, PostCard, PostType, ReadingGoalCard (+6 more)

### Community 8 - "AppRoute"
Cohesion: 0.06
Nodes (33): Content, URLQueryItem, AppRoute, book, composer, followers, libraryBook, post (+25 more)

### Community 9 - "ReadingStatus"
Cohesion: 0.05
Nodes (28): Foundation, ModelContext, Observation, SwiftData, BookCache, CachedBook, ReadingTimer, SearchViewModelTests (+20 more)

### Community 11 - "Yomora Application Target"
Cohesion: 0.19
Nodes (13): iOS 17 Deployment Target, Local API Endpoint at Port 8080, Swift 6 Configuration, Yomora Application Target, Yomora iOS Project, Yomora UI Test Target, Yomora Unit Test Target, Yomora Book-and-Sprout Symbol (+5 more)

### Community 12 - "View"
Cohesion: 0.08
Nodes (29): CaseIterable, ColorScheme, UInt, UIUserInterfaceStyle, ThemePicker, AccentChoice, forest, gold (+21 more)

### Community 13 - "TodayView"
Cohesion: 0.16
Nodes (7): TodayViewModel, Double, LibraryBook, TodayView, Bool, TodayAPIClient, TodayViewModelTests

### Community 17 - "TextDraft"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Quando eu clico para finalizar sessão e aparece o resumo da leitura, e clico na seta para voltar ela volta para o timer em contagem ao invés de voltar para a tela "Hoje", Source Nodes

### Community 19 - "State"
Cohesion: 0.09
Nodes (24): NoopReadingActivityManager, ReadingActivityManaging, ReadingSessionNoteField, ReadingSessionView, SessionSummaryView, Bool, FocusState, LibraryBook (+16 more)

### Community 20 - "TextDraft"
Cohesion: 0.24
Nodes (14): Axis, UIKeyboardType, UITextContentType, DraftActionButton, DraftTextField, PrimaryButton, SecondaryButton, Bool (+6 more)

### Community 24 - "SwiftUI"
Cohesion: 0.20
Nodes (7): PostType, SwiftUI, SplashView, PostComposerView, String, StatisticsView, String

### Community 25 - "AnyEncodable"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: A parte de escrever uma nota sobre a sessão de leitura está quase invísivel, não da para saber o que está escrito no placeholder, Source Nodes

### Community 26 - "BookComponents.swift"
Cohesion: 0.19
Nodes (13): CryptoKit, Loader, BookCard, BookCover, BookCoverImageCache, ProgressRing, RatingView, CGFloat (+5 more)

### Community 27 - "LibraryBookDetailsView"
Cohesion: 0.40
Nodes (3): FollowersView, ProfileEditView, UUID

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
- **101 isolated node(s):** `book`, `libraryBook`, `reading`, `sessionSummary`, `post` (+96 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **35 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Work-memory lessons

**Preferred sources** — corroborated by past sessions; start here.
- `ReadingSessionNoteField` (2× useful, score=1.998392598)
- `MainTabView` (2× useful, score=1.997500949)

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `APIClientProtocol` connect `AppContainer` to `Equatable`, `APIClientProtocol`, `APIError`, `SearchViewModel`, `TokenPair`, `AppRoute`, `ReadingStatus`, `TodayView`, `State`, `TextDraft`, `SwiftUI`, `LibraryBookDetailsView`?**
  _High betweenness centrality (0.128) - this node is a cross-community bridge._
- **Why does `Endpoint` connect `AppContainer` to `Equatable`, `APIClientProtocol`, `APIError`, `SearchViewModel`, `TokenPair`, `AppRoute`, `ReadingStatus`, `TodayView`?**
  _High betweenness centrality (0.072) - this node is a cross-community bridge._
- **Why does `SwiftUI` connect `SwiftUI` to `APIClientProtocol`, `APIError`, `SearchViewModel`, `AppContainer`, `StubAPIClient`, `AppRoute`, `ReadingStatus`, `View`, `TodayView`, `State`, `TextDraft`, `BookComponents.swift`, `LibraryBookDetailsView`, `OnboardingView.swift`, `AuthenticationView`, `SettingsView`?**
  _High betweenness centrality (0.060) - this node is a cross-community bridge._
- **Are the 15 inferred relationships involving `APIClientProtocol` (e.g. with `.load()` and `.add()`) actually correct?**
  _`APIClientProtocol` has 15 INFERRED edges - model-reasoned connections that need verification._
- **Are the 27 inferred relationships involving `Endpoint` (e.g. with `login()` and `refreshUser()`) actually correct?**
  _`Endpoint` has 27 INFERRED edges - model-reasoned connections that need verification._
- **Are the 5 inferred relationships involving `AppContainer` (e.g. with `.loadDetails()` and `.save()`) actually correct?**
  _`AppContainer` has 5 INFERRED edges - model-reasoned connections that need verification._
- **What connects `book`, `libraryBook`, `reading` to the rest of the system?**
  _101 weakly-connected nodes found - possible documentation gaps or missing edges._
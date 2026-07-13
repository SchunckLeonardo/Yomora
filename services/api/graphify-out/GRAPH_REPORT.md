# Graph Report - .  (2026-07-13)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1286 nodes · 2996 edges · 66 communities (57 shown, 9 thin omitted)
- Extraction: 92% EXTRACTED · 8% INFERRED · 0% AMBIGUOUS · INFERRED: 247 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `4c8f6ba2`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BlockedUser
- Shelf
- CurrentUser
- JpaRepository
- GlobalExceptionHandler.java
- PersistenceQueryIntegrationTest
- ReadingGoal
- PasswordResetToken
- JpaReviewRepository
- ReadingStatus
- BookCatalogRepository
- Note
- UserController.java
- UserRepository
- AuthenticationService
- Transactional
- JpaUserRepositoryAdapter
- .update
- RefreshToken
- SocialService
- BookCatalogAggregator
- LibraryService
- Review
- PostType
- Override
- NoteRepository
- Post
- Comment
- GoogleBooksProvider.java
- AuthController.java
- PostController.java
- SecurityConfiguration.java
- User
- .finish
- ReviewController.java
- OpenLibraryProvider.java
- LocalPasswordResetMailbox
- UserBook
- BookSearchResult
- NoteController.java
- BookCatalogController.java
- SecurityProperties
- .editPost
- BookWorkEntity
- .search
- PasswordResetService
- Yomora API Application Configuration
- AuthApiIntegrationTest.java
- InMemoryUserRepository
- ReviewAndNoteServiceTest.java
- TestcontainersConfiguration.java
- YomoraApiApplicationTests.java
- YomoraApiApplication
- LibraryConfiguration.java
- ApplicationConfiguration.java
- ModularArchitectureTest.java
- gradlew
- TestYomoraApiApplication
- Entity
- Table
- Override
- Repository
- Transactional

## God Nodes (most connected - your core abstractions)
1. `User` - 43 edges
2. `Post` - 41 edges
3. `UserBook` - 39 edges
4. `CurrentUser` - 36 edges
5. `Shelf` - 34 edges
6. `SocialService` - 33 edges
7. `BookCatalogRepository` - 32 edges
8. `UserRepository` - 32 edges
9. `Review` - 31 edges
10. `BookSearchResult` - 29 edges

## Surprising Connections (you probably didn't know these)
- `BookCatalogAggregator` --references--> `BookCatalogRepository`  [EXTRACTED]
  src/main/java/com/yomora/catalog/application/BookCatalogAggregator.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java
- `BookCatalogController` --references--> `BookCatalogAggregator`  [EXTRACTED]
  src/main/java/com/yomora/catalog/web/BookCatalogController.java → src/main/java/com/yomora/catalog/application/BookCatalogAggregator.java
- `InMemoryCatalogRepository` --references--> `BookCandidate`  [EXTRACTED]
  src/test/java/com/yomora/catalog/application/BookCatalogAggregatorTest.java → src/main/java/com/yomora/catalog/domain/BookCandidate.java
- `BookCatalogController` --references--> `BookCatalogRepository`  [EXTRACTED]
  src/main/java/com/yomora/catalog/web/BookCatalogController.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java
- `LibraryService` --references--> `BookCatalogRepository`  [EXTRACTED]
  src/main/java/com/yomora/library/application/LibraryService.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java

## Import Cycles
- None detected.

## Communities (66 total, 9 thin omitted)

### Community 0 - "BlockedUser"
Cohesion: 0.06
Nodes (31): Transactional, ModerationService, BlockedUser, ModerationRepository, Report, Bean, Configuration, ModerationConfiguration (+23 more)

### Community 1 - "Shelf"
Cohesion: 0.07
Nodes (26): Transactional, ShelfService, Shelf, ShelfRepository, Override, Repository, JpaShelfRepository, Entity (+18 more)

### Community 2 - "CurrentUser"
Cohesion: 0.05
Nodes (32): ReadingSessionSummary, StatisticsSummary, ReadingSession, DailyReadingProjection, Override, Repository, JpaReadingSessionRepository, Entity (+24 more)

### Community 3 - "JpaRepository"
Cohesion: 0.06
Nodes (24): BookEditionEntity, JpaRepository, Pageable, PostEntity, Query, SpringDataBookEditionRepository, SpringDataBookWorkRepository, CommentEntity (+16 more)

### Community 4 - "GlobalExceptionHandler.java"
Cohesion: 0.06
Nodes (20): ExceptionHandler, HttpStatus, MethodArgumentNotValidException, ProblemDetail, RestControllerAdvice, IdentityConflictException, InvalidCredentialsException, InvalidPasswordResetTokenException (+12 more)

### Community 5 - "PersistenceQueryIntegrationTest"
Cohesion: 0.11
Nodes (19): BookCandidate, BookCatalogRepository, BookSearchResult, EntityManager, Import, JdbcTemplate, Override, Post (+11 more)

### Community 6 - "ReadingGoal"
Cohesion: 0.10
Nodes (17): PutMapping, Transactional, ReadingGoalService, ReadingGoal, Override, Repository, JpaReadingGoalRepository, Entity (+9 more)

### Community 7 - "PasswordResetToken"
Cohesion: 0.11
Nodes (14): Transactional, PasswordResetToken, PasswordResetTokenRepository, Override, Repository, JpaPasswordResetTokenRepositoryAdapter, Entity, Table (+6 more)

### Community 8 - "JpaReviewRepository"
Cohesion: 0.16
Nodes (7): Override, Repository, JpaReviewRepository, Entity, Table, ReviewEntity, SpringDataReviewRepository

### Community 9 - "ReadingStatus"
Cohesion: 0.12
Nodes (13): ReadingStatus, ABANDONED, FINISHED, PAUSED, READING, WANT_TO_READ, Override, Repository (+5 more)

### Community 10 - "BookCatalogRepository"
Cohesion: 0.21
Nodes (10): BookCatalogRepository, UserBookRepository, ReadingConsistencyCalculator, ReadingSessionService, StatisticsService, ReadingGoalRepository, ReadingSessionRepository, Bean (+2 more)

### Community 11 - "Note"
Cohesion: 0.14
Nodes (9): Note, Override, Repository, JpaNoteRepository, Entity, Table, NoteEntity, SpringDataNoteRepository (+1 more)

### Community 12 - "UserController.java"
Cohesion: 0.15
Nodes (15): ProfileMetrics, Service, Transactional, ProfileMetricsService, Authentication, DeleteMapping, GetMapping, PatchMapping (+7 more)

### Community 13 - "UserRepository"
Cohesion: 0.15
Nodes (11): RegisterCommand, Bean, Configuration, PasswordEncoder, UserApplicationConfiguration, PasswordEncoder, UserRegistrationService, UserRepository (+3 more)

### Community 14 - "AuthenticationService"
Cohesion: 0.16
Nodes (7): AccessTokenIssuer, AuthenticationService, PasswordEncoder, SecureRandom, Service, Transactional, TokenPair

### Community 15 - "Transactional"
Cohesion: 0.15
Nodes (9): Transactional, FollowController, Authentication, DeleteMapping, GetMapping, PostMapping, RequestMapping, ResponseEntity (+1 more)

### Community 16 - "JpaUserRepositoryAdapter"
Cohesion: 0.15
Nodes (7): Override, Repository, JpaUserRepositoryAdapter, SpringDataUserRepository, Entity, Table, UserEntity

### Community 17 - ".update"
Cohesion: 0.17
Nodes (7): UpdateProfileCommand, Service, Transactional, UserProfileService, UserNotFoundException, Test, UserProfileServiceTest

### Community 18 - "RefreshToken"
Cohesion: 0.16
Nodes (9): RefreshToken, RefreshTokenRepository, Override, Repository, JpaRefreshTokenRepositoryAdapter, Entity, Table, RefreshTokenEntity (+1 more)

### Community 19 - "SocialService"
Cohesion: 0.16
Nodes (8): Component, Override, SocialPublisherAdapter, SocialService, SocialRepository, Bean, Configuration, SocialConfiguration

### Community 21 - "BookCatalogAggregator"
Cohesion: 0.17
Nodes (9): Cacheable, EnableCaching, FunctionalInterface, BookCatalogAggregator, BookProvider, BookProviderQuery, CatalogConfiguration, Bean (+1 more)

### Community 22 - "LibraryService"
Cohesion: 0.14
Nodes (13): Transactional, LibraryService, AddBookRequest, Authentication, DeleteMapping, GetMapping, PatchMapping, PostMapping (+5 more)

### Community 23 - "Review"
Cohesion: 0.17
Nodes (5): Transactional, Review, ReviewRepository, InMemoryReviewRepository, Override

### Community 24 - "PostType"
Cohesion: 0.14
Nodes (13): PostType, NOTE, PROGRESS, QUOTE, RECOMMENDATION, REVIEW, Visibility, FOLLOWERS (+5 more)

### Community 25 - "Override"
Cohesion: 0.17
Nodes (6): FixedCatalogRepository, InMemoryReadingSessionRepository, Override, Test, MutableClock, ReadingSessionServiceTest

### Community 26 - "NoteRepository"
Cohesion: 0.17
Nodes (7): Transactional, NoteService, SocialPublisher, NoteRepository, Bean, Configuration, ReviewConfiguration

### Community 28 - "Comment"
Cohesion: 0.20
Nodes (3): Comment, InMemorySocialRepository, Override

### Community 29 - "GoogleBooksProvider.java"
Cohesion: 0.20
Nodes (10): GoogleBooksProvider, GoogleBooksResponse, GoogleImageLinks, GoogleIndustryIdentifier, GoogleVolume, GoogleVolumeInfo, Builder, Component (+2 more)

### Community 30 - "AuthController.java"
Cohesion: 0.23
Nodes (10): AuthController, PostMapping, RequestMapping, ResponseEntity, RestController, LoginRequest, PasswordResetConfirmRequest, PasswordResetRequest (+2 more)

### Community 31 - "PostController.java"
Cohesion: 0.25
Nodes (11): CommentRequest, CreatePostRequest, EditPostRequest, Authentication, DeleteMapping, PatchMapping, PostMapping, RequestMapping (+3 more)

### Community 32 - "SecurityConfiguration.java"
Cohesion: 0.24
Nodes (10): CorsConfigurationSource, HttpSecurity, JwtDecoder, SecretKey, SecurityFilterChain, Bean, Configuration, JwtEncoder (+2 more)

### Community 33 - "User"
Cohesion: 0.24
Nodes (5): User, InMemoryUserRepository, Override, InMemoryUserRepository, Override

### Community 34 - ".finish"
Cohesion: 0.10
Nodes (6): ConsistencySummary, DailyReading, Transactional, Transactional, Test, ReadingConsistencyCalculatorTest

### Community 35 - "ReviewController.java"
Cohesion: 0.21
Nodes (12): ReviewService, Authentication, DeleteMapping, GetMapping, PatchMapping, PostMapping, RequestMapping, ResponseEntity (+4 more)

### Community 36 - "OpenLibraryProvider.java"
Cohesion: 0.19
Nodes (8): BookProviderUnavailableException, Builder, Component, Override, RestClient, OpenLibraryDocument, OpenLibraryProvider, OpenLibraryResponse

### Community 37 - "LocalPasswordResetMailbox"
Cohesion: 0.21
Nodes (10): Component, Override, Profile, LocalPasswordResetMailbox, GetMapping, Profile, RequestMapping, RestController (+2 more)

### Community 38 - "UserBook"
Cohesion: 0.18
Nodes (6): UserBook, InMemoryUserBookRepository, Override, Test, LibraryServiceTest, InMemoryUserBookRepository

### Community 39 - "BookSearchResult"
Cohesion: 0.25
Nodes (4): BookCandidate, BookSearchResult, Override, FixedCatalogRepository

### Community 41 - "NoteController.java"
Cohesion: 0.26
Nodes (9): Authentication, DeleteMapping, GetMapping, PostMapping, RequestMapping, ResponseEntity, RestController, NoteController (+1 more)

### Community 42 - "BookCatalogController.java"
Cohesion: 0.23
Nodes (7): BookCatalogController, GetMapping, PostMapping, RequestMapping, ResponseEntity, RestController, ManualBookRequest

### Community 43 - "SecurityProperties"
Cohesion: 0.29
Nodes (7): ConfigurationProperties, Component, JwtEncoder, Override, JwtAccessTokenIssuer, SecurityProperties, Validated

### Community 45 - "BookWorkEntity"
Cohesion: 0.27
Nodes (6): Entity, BookEditionEntity, Entity, Table, BookWorkEntity, Table

### Community 46 - ".search"
Cohesion: 0.36
Nodes (4): SearchBooksQuery, BookCatalogAggregatorTest, InMemoryCatalogRepository, Test

### Community 47 - "PasswordResetService"
Cohesion: 0.14
Nodes (10): PasswordResetNotifier, PasswordEncoder, SecureRandom, Service, PasswordResetService, DiscardingPasswordResetNotifier, Component, Override (+2 more)

### Community 48 - "Yomora API Application Configuration"
Cohesion: 0.24
Nodes (10): Caffeine Cache, CORS Allowed Origins Policy, Flyway Migrations, JPA Schema Validation, JWT Security Configuration, Health and Info Management Endpoints, PostgreSQL Datasource, Sanitized Server Error Responses (+2 more)

### Community 49 - "AuthApiIntegrationTest.java"
Cohesion: 0.43
Nodes (6): AutoConfigureMockMvc, MockMvc, AuthApiIntegrationTest, Import, SpringBootTest, Test

### Community 51 - "ReviewAndNoteServiceTest.java"
Cohesion: 0.46
Nodes (3): CapturingPublisher, Test, ReviewAndNoteServiceTest

### Community 52 - "TestcontainersConfiguration.java"
Cohesion: 0.48
Nodes (5): PostgreSQLContainer, ServiceConnection, Bean, TestcontainersConfiguration, TestConfiguration

### Community 53 - "YomoraApiApplicationTests.java"
Cohesion: 0.53
Nodes (4): Import, SpringBootTest, Test, YomoraApiApplicationTests

### Community 54 - "YomoraApiApplication"
Cohesion: 0.60
Nodes (3): ConfigurationPropertiesScan, SpringBootApplication, YomoraApiApplication

### Community 55 - "LibraryConfiguration.java"
Cohesion: 0.60
Nodes (3): Bean, Configuration, LibraryConfiguration

### Community 56 - "ApplicationConfiguration.java"
Cohesion: 0.60
Nodes (3): ApplicationConfiguration, Bean, Configuration

### Community 57 - "ModularArchitectureTest.java"
Cohesion: 0.83
Nodes (3): AnalyzeClasses, ArchRule, ModularArchitectureTest

### Community 58 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **18 isolated node(s):** `WANT_TO_READ`, `READING`, `PAUSED`, `FINISHED`, `ABANDONED` (+13 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **9 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `CurrentUser` connect `CurrentUser` to `BlockedUser`, `Shelf`, `ReviewController.java`, `ReadingGoal`, `NoteController.java`, `UserController.java`, `Transactional`, `LibraryService`, `PostController.java`?**
  _High betweenness centrality (0.238) - this node is a cross-community bridge._
- **Why does `User` connect `User` to `PasswordResetToken`, `SecurityProperties`, `UserController.java`, `UserRepository`, `AuthenticationService`, `PasswordResetService`, `JpaUserRepositoryAdapter`, `.update`, `InMemoryUserRepository`?**
  _High betweenness centrality (0.083) - this node is a cross-community bridge._
- **Why does `UserRepository` connect `UserRepository` to `User`, `PasswordResetToken`, `UserController.java`, `AuthenticationService`, `PasswordResetService`, `JpaUserRepositoryAdapter`, `.update`, `InMemoryUserRepository`?**
  _High betweenness centrality (0.077) - this node is a cross-community bridge._
- **What connects `WANT_TO_READ`, `READING`, `PAUSED` to the rest of the system?**
  _18 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BlockedUser` be split into smaller, more focused modules?**
  _Cohesion score 0.06050228310502283 - nodes in this community are weakly interconnected._
- **Should `Shelf` be split into smaller, more focused modules?**
  _Cohesion score 0.07082494969818913 - nodes in this community are weakly interconnected._
- **Should `CurrentUser` be split into smaller, more focused modules?**
  _Cohesion score 0.05115089514066496 - nodes in this community are weakly interconnected._
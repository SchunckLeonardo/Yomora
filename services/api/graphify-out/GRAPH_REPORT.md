# Graph Report - .  (2026-07-13)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1284 nodes · 2991 edges · 73 communities (62 shown, 11 thin omitted)
- Extraction: 92% EXTRACTED · 8% INFERRED · 0% AMBIGUOUS · INFERRED: 247 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `e8be4bb0`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BlockedUser
- Shelf
- JpaRepository
- PasswordResetToken
- GlobalExceptionHandler.java
- ReadingGoal
- PersistenceQueryIntegrationTest
- Review
- ReadingStatus
- SecurityProperties
- BookCatalogRepository
- PostType
- Note
- UserController.java
- UserBook
- RefreshToken
- .update
- AuthController.java
- BookCatalogAggregator
- AuthenticationService
- .summary
- NoteRepository
- SocialService
- UserRepository
- SocialRepository
- InMemorySocialRepository
- PostController.java
- GoogleBooksProvider.java
- User
- LibraryService
- ReviewRepository
- FollowController.java
- ReadingSessionController.java
- ReviewController.java
- OpenLibraryProvider.java
- LocalPasswordResetMailbox
- .add
- JpaUserRepositoryAdapter
- ReadingSession
- JpaReadingSessionRepository
- BookSearchResult
- NoteController.java
- BookCatalogController.java
- CurrentUser
- Post
- BookWorkEntity
- .search
- Yomora API Application Configuration
- AuthApiIntegrationTest.java
- InMemoryUserRepository
- ReviewAndNoteServiceTest.java
- TestcontainersConfiguration.java
- ProfileMetricsServiceTest.java
- InMemoryUserBookRepository
- UserApplicationConfiguration.java
- YomoraApiApplicationTests.java
- YomoraApiApplication
- LibraryConfiguration.java
- DailyReadingProjection
- ApplicationConfiguration.java
- ModularArchitectureTest.java
- gradlew
- .finish
- TestYomoraApiApplication
- Entity
- Table
- Override
- Repository
- Transactional
- SocialConfiguration.java

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

## Communities (73 total, 11 thin omitted)

### Community 0 - "BlockedUser"
Cohesion: 0.06
Nodes (31): Transactional, ModerationService, BlockedUser, ModerationRepository, Report, Bean, Configuration, ModerationConfiguration (+23 more)

### Community 1 - "Shelf"
Cohesion: 0.07
Nodes (26): Transactional, ShelfService, Shelf, ShelfRepository, Override, Repository, JpaShelfRepository, Entity (+18 more)

### Community 2 - "JpaRepository"
Cohesion: 0.06
Nodes (24): BookEditionEntity, JpaRepository, Pageable, PostEntity, Query, SpringDataBookEditionRepository, SpringDataBookWorkRepository, CommentEntity (+16 more)

### Community 3 - "PasswordResetToken"
Cohesion: 0.06
Nodes (26): PasswordResetNotifier, PasswordEncoder, SecureRandom, Service, Transactional, PasswordResetService, PasswordResetToken, PasswordResetTokenRepository (+18 more)

### Community 4 - "GlobalExceptionHandler.java"
Cohesion: 0.06
Nodes (20): ExceptionHandler, HttpStatus, MethodArgumentNotValidException, ProblemDetail, RestControllerAdvice, IdentityConflictException, InvalidCredentialsException, InvalidPasswordResetTokenException (+12 more)

### Community 5 - "ReadingGoal"
Cohesion: 0.10
Nodes (17): PutMapping, Transactional, ReadingGoalService, ReadingGoal, Override, Repository, JpaReadingGoalRepository, Entity (+9 more)

### Community 6 - "PersistenceQueryIntegrationTest"
Cohesion: 0.12
Nodes (19): BookCandidate, BookCatalogRepository, BookSearchResult, EntityManager, Import, JdbcTemplate, Override, Post (+11 more)

### Community 7 - "Review"
Cohesion: 0.14
Nodes (10): Review, Override, Repository, JpaReviewRepository, Entity, Table, ReviewEntity, SpringDataReviewRepository (+2 more)

### Community 8 - "ReadingStatus"
Cohesion: 0.12
Nodes (13): ReadingStatus, ABANDONED, FINISHED, PAUSED, READING, WANT_TO_READ, Override, Repository (+5 more)

### Community 9 - "SecurityProperties"
Cohesion: 0.13
Nodes (17): ConfigurationProperties, CorsConfigurationSource, HttpSecurity, JwtDecoder, SecretKey, SecurityFilterChain, Component, JwtEncoder (+9 more)

### Community 10 - "BookCatalogRepository"
Cohesion: 0.21
Nodes (10): BookCatalogRepository, UserBookRepository, ReadingConsistencyCalculator, ReadingSessionService, StatisticsService, ReadingGoalRepository, ReadingSessionRepository, Bean (+2 more)

### Community 11 - "PostType"
Cohesion: 0.14
Nodes (13): PostType, NOTE, PROGRESS, QUOTE, RECOMMENDATION, REVIEW, Visibility, FOLLOWERS (+5 more)

### Community 12 - "Note"
Cohesion: 0.14
Nodes (9): Note, Override, Repository, JpaNoteRepository, Entity, Table, NoteEntity, SpringDataNoteRepository (+1 more)

### Community 13 - "UserController.java"
Cohesion: 0.16
Nodes (15): ProfileMetrics, Service, Transactional, ProfileMetricsService, Authentication, DeleteMapping, GetMapping, PatchMapping (+7 more)

### Community 14 - "UserBook"
Cohesion: 0.16
Nodes (7): UserBook, FixedCatalogRepository, InMemoryUserBookRepository, Override, Test, MutableClock, ReadingSessionServiceTest

### Community 15 - "RefreshToken"
Cohesion: 0.15
Nodes (9): RefreshToken, RefreshTokenRepository, Override, Repository, JpaRefreshTokenRepositoryAdapter, Entity, Table, RefreshTokenEntity (+1 more)

### Community 16 - ".update"
Cohesion: 0.17
Nodes (7): UpdateProfileCommand, Service, Transactional, UserProfileService, UserNotFoundException, Test, UserProfileServiceTest

### Community 17 - "AuthController.java"
Cohesion: 0.21
Nodes (11): TokenPair, AuthController, PostMapping, RequestMapping, ResponseEntity, RestController, LoginRequest, PasswordResetConfirmRequest (+3 more)

### Community 18 - "BookCatalogAggregator"
Cohesion: 0.17
Nodes (9): Cacheable, EnableCaching, FunctionalInterface, BookCatalogAggregator, BookProvider, BookProviderQuery, CatalogConfiguration, Bean (+1 more)

### Community 19 - "AuthenticationService"
Cohesion: 0.18
Nodes (6): AccessTokenIssuer, AuthenticationService, PasswordEncoder, SecureRandom, Service, Transactional

### Community 20 - ".summary"
Cohesion: 0.14
Nodes (5): ConsistencySummary, DailyReading, Transactional, Test, ReadingConsistencyCalculatorTest

### Community 21 - "NoteRepository"
Cohesion: 0.20
Nodes (7): Transactional, NoteService, SocialPublisher, NoteRepository, Bean, Configuration, ReviewConfiguration

### Community 22 - "SocialService"
Cohesion: 0.23
Nodes (4): Component, Override, SocialPublisherAdapter, SocialService

### Community 23 - "UserRepository"
Cohesion: 0.19
Nodes (7): RegisterCommand, PasswordEncoder, UserRegistrationService, UserRepository, BCryptPasswordEncoder, Test, UserRegistrationServiceTest

### Community 24 - "SocialRepository"
Cohesion: 0.20
Nodes (3): Transactional, Comment, SocialRepository

### Community 25 - "InMemorySocialRepository"
Cohesion: 0.19
Nodes (4): InMemorySocialRepository, Override, Test, SocialServiceTest

### Community 26 - "PostController.java"
Cohesion: 0.18
Nodes (11): CommentRequest, CreatePostRequest, EditPostRequest, Authentication, DeleteMapping, PatchMapping, PostMapping, RequestMapping (+3 more)

### Community 27 - "GoogleBooksProvider.java"
Cohesion: 0.20
Nodes (10): GoogleBooksProvider, GoogleBooksResponse, GoogleImageLinks, GoogleIndustryIdentifier, GoogleVolume, GoogleVolumeInfo, Builder, Component (+2 more)

### Community 28 - "User"
Cohesion: 0.21
Nodes (6): User, Entity, Table, UserEntity, InMemoryUserRepository, Override

### Community 29 - "LibraryService"
Cohesion: 0.19
Nodes (12): LibraryService, AddBookRequest, Authentication, DeleteMapping, GetMapping, PatchMapping, PostMapping, RequestMapping (+4 more)

### Community 30 - "ReviewRepository"
Cohesion: 0.22
Nodes (3): Transactional, ReviewService, ReviewRepository

### Community 31 - "FollowController.java"
Cohesion: 0.15
Nodes (8): FollowController, Authentication, DeleteMapping, GetMapping, PostMapping, RequestMapping, ResponseEntity, RestController

### Community 32 - "ReadingSessionController.java"
Cohesion: 0.21
Nodes (11): ReadingSessionSummary, FinishRequest, Authentication, GetMapping, PatchMapping, PostMapping, RequestMapping, ResponseEntity (+3 more)

### Community 33 - "ReviewController.java"
Cohesion: 0.21
Nodes (11): Authentication, DeleteMapping, GetMapping, PatchMapping, PostMapping, RequestMapping, ResponseEntity, RestController (+3 more)

### Community 34 - "OpenLibraryProvider.java"
Cohesion: 0.19
Nodes (8): BookProviderUnavailableException, Builder, Component, Override, RestClient, OpenLibraryDocument, OpenLibraryProvider, OpenLibraryResponse

### Community 35 - "LocalPasswordResetMailbox"
Cohesion: 0.21
Nodes (10): Component, Override, Profile, LocalPasswordResetMailbox, GetMapping, Profile, RequestMapping, RestController (+2 more)

### Community 36 - ".add"
Cohesion: 0.24
Nodes (3): Transactional, Test, LibraryServiceTest

### Community 37 - "JpaUserRepositoryAdapter"
Cohesion: 0.22
Nodes (4): Override, Repository, JpaUserRepositoryAdapter, SpringDataUserRepository

### Community 38 - "ReadingSession"
Cohesion: 0.22
Nodes (5): ReadingSession, Entity, Table, ReadingSessionEntity, InMemoryReadingSessionRepository

### Community 39 - "JpaReadingSessionRepository"
Cohesion: 0.22
Nodes (5): Override, Repository, JpaReadingSessionRepository, Query, SpringDataReadingSessionRepository

### Community 40 - "BookSearchResult"
Cohesion: 0.25
Nodes (4): BookCandidate, BookSearchResult, Override, FixedCatalogRepository

### Community 41 - "NoteController.java"
Cohesion: 0.26
Nodes (9): Authentication, DeleteMapping, GetMapping, PostMapping, RequestMapping, ResponseEntity, RestController, NoteController (+1 more)

### Community 42 - "BookCatalogController.java"
Cohesion: 0.23
Nodes (7): BookCatalogController, GetMapping, PostMapping, RequestMapping, ResponseEntity, RestController, ManualBookRequest

### Community 43 - "CurrentUser"
Cohesion: 0.26
Nodes (8): Authentication, GetMapping, RequestMapping, RestController, StatisticsController, CurrentUser, Authentication, Component

### Community 45 - "BookWorkEntity"
Cohesion: 0.27
Nodes (6): Entity, BookEditionEntity, Entity, Table, BookWorkEntity, Table

### Community 46 - ".search"
Cohesion: 0.36
Nodes (4): SearchBooksQuery, BookCatalogAggregatorTest, InMemoryCatalogRepository, Test

### Community 47 - "Yomora API Application Configuration"
Cohesion: 0.24
Nodes (10): Caffeine Cache, CORS Allowed Origins Policy, Flyway Migrations, JPA Schema Validation, JWT Security Configuration, Health and Info Management Endpoints, PostgreSQL Datasource, Sanitized Server Error Responses (+2 more)

### Community 48 - "AuthApiIntegrationTest.java"
Cohesion: 0.43
Nodes (6): AutoConfigureMockMvc, MockMvc, AuthApiIntegrationTest, Import, SpringBootTest, Test

### Community 50 - "ReviewAndNoteServiceTest.java"
Cohesion: 0.46
Nodes (3): CapturingPublisher, Test, ReviewAndNoteServiceTest

### Community 51 - "TestcontainersConfiguration.java"
Cohesion: 0.48
Nodes (5): PostgreSQLContainer, ServiceConnection, Bean, TestcontainersConfiguration, TestConfiguration

### Community 52 - "ProfileMetricsServiceTest.java"
Cohesion: 0.43
Nodes (3): StatisticsSummary, Test, ProfileMetricsServiceTest

### Community 54 - "UserApplicationConfiguration.java"
Cohesion: 0.53
Nodes (4): Bean, Configuration, PasswordEncoder, UserApplicationConfiguration

### Community 55 - "YomoraApiApplicationTests.java"
Cohesion: 0.53
Nodes (4): Import, SpringBootTest, Test, YomoraApiApplicationTests

### Community 56 - "YomoraApiApplication"
Cohesion: 0.60
Nodes (3): ConfigurationPropertiesScan, SpringBootApplication, YomoraApiApplication

### Community 57 - "LibraryConfiguration.java"
Cohesion: 0.60
Nodes (3): Bean, Configuration, LibraryConfiguration

### Community 59 - "ApplicationConfiguration.java"
Cohesion: 0.60
Nodes (3): ApplicationConfiguration, Bean, Configuration

### Community 60 - "ModularArchitectureTest.java"
Cohesion: 0.83
Nodes (3): AnalyzeClasses, ArchRule, ModularArchitectureTest

### Community 61 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 72 - "SocialConfiguration.java"
Cohesion: 0.60
Nodes (3): Bean, Configuration, SocialConfiguration

## Knowledge Gaps
- **18 isolated node(s):** `WANT_TO_READ`, `READING`, `PAUSED`, `FINISHED`, `ABANDONED` (+13 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **11 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `CurrentUser` connect `CurrentUser` to `BlockedUser`, `Shelf`, `ReadingSessionController.java`, `ReviewController.java`, `ReadingGoal`, `NoteController.java`, `UserController.java`, `PostController.java`, `LibraryService`, `FollowController.java`?**
  _High betweenness centrality (0.238) - this node is a cross-community bridge._
- **Why does `User` connect `User` to `PasswordResetToken`, `JpaUserRepositoryAdapter`, `SecurityProperties`, `UserController.java`, `.update`, `InMemoryUserRepository`, `AuthenticationService`, `UserRepository`?**
  _High betweenness centrality (0.083) - this node is a cross-community bridge._
- **Why does `UserRepository` connect `UserRepository` to `PasswordResetToken`, `JpaUserRepositoryAdapter`, `UserController.java`, `.update`, `InMemoryUserRepository`, `AuthenticationService`, `UserApplicationConfiguration.java`, `User`?**
  _High betweenness centrality (0.077) - this node is a cross-community bridge._
- **What connects `WANT_TO_READ`, `READING`, `PAUSED` to the rest of the system?**
  _18 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BlockedUser` be split into smaller, more focused modules?**
  _Cohesion score 0.06050228310502283 - nodes in this community are weakly interconnected._
- **Should `Shelf` be split into smaller, more focused modules?**
  _Cohesion score 0.07082494969818913 - nodes in this community are weakly interconnected._
- **Should `JpaRepository` be split into smaller, more focused modules?**
  _Cohesion score 0.05961538461538462 - nodes in this community are weakly interconnected._
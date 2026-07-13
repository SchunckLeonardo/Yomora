# Graph Report - .  (2026-07-13)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1298 nodes · 3012 edges · 65 communities (56 shown, 9 thin omitted)
- Extraction: 92% EXTRACTED · 8% INFERRED · 0% AMBIGUOUS · INFERRED: 249 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `78e8d951`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Shelf
- BlockedUser
- BookCatalogRepository
- JpaRepository
- GlobalExceptionHandler.java
- PasswordResetToken
- PersistenceQueryIntegrationTest
- GoogleBooksProvider.java
- ReadingGoal
- SocialService
- Review
- Note
- UserBookRepository
- UserRepository
- PostType
- PostController.java
- UserBook
- UserBookEntity
- Post
- AuthenticationService
- UserController.java
- RefreshToken
- .summary
- .finish
- ReadingSession
- ReviewRepository
- CurrentUser
- JpaReadingSessionRepository
- NoteRepository
- SecurityConfiguration.java
- LibraryController.java
- ReviewController.java
- .register
- User
- LocalPasswordResetMailbox
- JpaUserRepositoryAdapter
- FollowController.java
- LibraryService
- AuthController.java
- LibraryServiceTest.java
- SecurityProperties
- UserRegistrationService
- ReadingSessionService.java
- Yomora API Application Configuration
- AuthApiIntegrationTest.java
- InMemoryUserRepository
- InMemoryUserRepository
- ReviewAndNoteServiceTest.java
- TestcontainersConfiguration.java
- ReadingStatus
- YomoraApiApplicationTests.java
- YomoraApiApplication
- ApplicationConfiguration.java
- SocialRepository
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
- `LibraryService` --references--> `BookCatalogRepository`  [EXTRACTED]
  src/main/java/com/yomora/library/application/LibraryService.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java
- `ReadingSessionService` --references--> `BookCatalogRepository`  [EXTRACTED]
  src/main/java/com/yomora/reading/application/ReadingSessionService.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java
- `StatisticsService` --references--> `BookCatalogRepository`  [EXTRACTED]
  src/main/java/com/yomora/reading/application/StatisticsService.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java
- `FixedCatalogRepository` --implements--> `BookCatalogRepository`  [EXTRACTED]
  src/test/java/com/yomora/reading/application/ReadingSessionServiceTest.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java
- `SpringDataBookWorkRepository` --references--> `BookWorkEntity`  [EXTRACTED]
  src/main/java/com/yomora/catalog/infrastructure/persistence/SpringDataBookWorkRepository.java → src/main/java/com/yomora/catalog/infrastructure/persistence/BookWorkEntity.java

## Import Cycles
- None detected.

## Communities (65 total, 9 thin omitted)

### Community 0 - "Shelf"
Cohesion: 0.07
Nodes (29): Transactional, ShelfService, Shelf, ShelfRepository, Bean, Configuration, LibraryConfiguration, Override (+21 more)

### Community 1 - "BlockedUser"
Cohesion: 0.06
Nodes (31): Transactional, ModerationService, BlockedUser, ModerationRepository, Report, Bean, Configuration, ModerationConfiguration (+23 more)

### Community 2 - "BookCatalogRepository"
Cohesion: 0.07
Nodes (31): Cacheable, EnableCaching, Entity, FunctionalInterface, BookCatalogAggregator, SearchBooksQuery, BookCandidate, BookCatalogRepository (+23 more)

### Community 3 - "JpaRepository"
Cohesion: 0.06
Nodes (25): BookEditionEntity, JpaRepository, Pageable, PostEntity, Query, SpringDataBookEditionRepository, SpringDataBookWorkRepository, Comment (+17 more)

### Community 4 - "GlobalExceptionHandler.java"
Cohesion: 0.06
Nodes (20): ExceptionHandler, HttpStatus, MethodArgumentNotValidException, ProblemDetail, RestControllerAdvice, IdentityConflictException, InvalidCredentialsException, InvalidPasswordResetTokenException (+12 more)

### Community 5 - "PasswordResetToken"
Cohesion: 0.07
Nodes (24): PasswordResetNotifier, PasswordEncoder, SecureRandom, Service, Transactional, PasswordResetService, PasswordResetToken, PasswordResetTokenRepository (+16 more)

### Community 6 - "PersistenceQueryIntegrationTest"
Cohesion: 0.09
Nodes (22): BookCandidate, BookCatalogRepository, BookSearchResult, EntityManager, Import, JdbcTemplate, Override, Post (+14 more)

### Community 7 - "GoogleBooksProvider.java"
Cohesion: 0.09
Nodes (23): BookProvider, BookProviderUnavailableException, GoogleBooksProvider, GoogleBooksResponse, GoogleImageLinks, GoogleIndustryIdentifier, GoogleVolume, GoogleVolumeInfo (+15 more)

### Community 8 - "ReadingGoal"
Cohesion: 0.11
Nodes (18): PutMapping, Transactional, ReadingGoalService, ReadingGoal, ReadingGoalRepository, Override, Repository, JpaReadingGoalRepository (+10 more)

### Community 9 - "SocialService"
Cohesion: 0.15
Nodes (5): Component, Override, SocialPublisherAdapter, Transactional, SocialService

### Community 10 - "Review"
Cohesion: 0.14
Nodes (10): Review, Override, Repository, JpaReviewRepository, Entity, Table, ReviewEntity, SpringDataReviewRepository (+2 more)

### Community 11 - "Note"
Cohesion: 0.14
Nodes (9): Note, Override, Repository, JpaNoteRepository, Entity, Table, NoteEntity, SpringDataNoteRepository (+1 more)

### Community 12 - "UserBookRepository"
Cohesion: 0.17
Nodes (10): Service, ProfileMetricsService, UserBookRepository, ConsistencySummary, ReadingConsistencyCalculator, StatisticsService, ReadingSessionRepository, Bean (+2 more)

### Community 13 - "UserRepository"
Cohesion: 0.15
Nodes (8): UpdateProfileCommand, Service, Transactional, UserProfileService, UserRepository, UserNotFoundException, Test, UserProfileServiceTest

### Community 14 - "PostType"
Cohesion: 0.14
Nodes (13): PostType, NOTE, PROGRESS, QUOTE, RECOMMENDATION, REVIEW, Visibility, FOLLOWERS (+5 more)

### Community 15 - "PostController.java"
Cohesion: 0.18
Nodes (12): CommentRequest, CreatePostRequest, EditPostRequest, Authentication, DeleteMapping, GetMapping, PatchMapping, PostMapping (+4 more)

### Community 16 - "UserBook"
Cohesion: 0.17
Nodes (7): UserBook, FixedCatalogRepository, InMemoryUserBookRepository, Override, Test, MutableClock, ReadingSessionServiceTest

### Community 17 - "UserBookEntity"
Cohesion: 0.15
Nodes (7): Override, Repository, JpaUserBookRepository, SpringDataUserBookRepository, Entity, Table, UserBookEntity

### Community 18 - "Post"
Cohesion: 0.17
Nodes (3): Post, InMemorySocialRepository, Override

### Community 19 - "AuthenticationService"
Cohesion: 0.16
Nodes (6): AccessTokenIssuer, AuthenticationService, PasswordEncoder, SecureRandom, Service, Transactional

### Community 20 - "UserController.java"
Cohesion: 0.19
Nodes (13): ProfileMetrics, Transactional, Authentication, DeleteMapping, GetMapping, PatchMapping, RequestMapping, ResponseEntity (+5 more)

### Community 21 - "RefreshToken"
Cohesion: 0.16
Nodes (9): RefreshToken, RefreshTokenRepository, Override, Repository, JpaRefreshTokenRepositoryAdapter, Entity, Table, RefreshTokenEntity (+1 more)

### Community 22 - ".summary"
Cohesion: 0.15
Nodes (9): DailyReading, StatisticsSummary, Authentication, GetMapping, RequestMapping, RestController, StatisticsController, Test (+1 more)

### Community 23 - ".finish"
Cohesion: 0.18
Nodes (12): ReadingSessionService, ReadingSessionSummary, FinishRequest, Authentication, GetMapping, PatchMapping, PostMapping, RequestMapping (+4 more)

### Community 24 - "ReadingSession"
Cohesion: 0.16
Nodes (6): Transactional, ReadingSession, Entity, Table, ReadingSessionEntity, InMemoryReadingSessionRepository

### Community 25 - "ReviewRepository"
Cohesion: 0.19
Nodes (3): Transactional, ReviewService, ReviewRepository

### Community 26 - "CurrentUser"
Cohesion: 0.18
Nodes (12): Authentication, DeleteMapping, GetMapping, PostMapping, RequestMapping, ResponseEntity, RestController, NoteController (+4 more)

### Community 27 - "JpaReadingSessionRepository"
Cohesion: 0.15
Nodes (6): DailyReadingProjection, Override, Repository, JpaReadingSessionRepository, Query, SpringDataReadingSessionRepository

### Community 28 - "NoteRepository"
Cohesion: 0.20
Nodes (7): Transactional, NoteService, SocialPublisher, NoteRepository, Bean, Configuration, ReviewConfiguration

### Community 29 - "SecurityConfiguration.java"
Cohesion: 0.24
Nodes (10): CorsConfigurationSource, HttpSecurity, JwtDecoder, SecretKey, SecurityFilterChain, Bean, Configuration, JwtEncoder (+2 more)

### Community 30 - "LibraryController.java"
Cohesion: 0.22
Nodes (11): AddBookRequest, Authentication, DeleteMapping, GetMapping, PatchMapping, PostMapping, RequestMapping, ResponseEntity (+3 more)

### Community 31 - "ReviewController.java"
Cohesion: 0.21
Nodes (11): Authentication, DeleteMapping, GetMapping, PatchMapping, PostMapping, RequestMapping, ResponseEntity, RestController (+3 more)

### Community 32 - ".register"
Cohesion: 0.20
Nodes (6): RegisterCommand, TokenPair, RegisterRequest, BCryptPasswordEncoder, Test, UserRegistrationServiceTest

### Community 33 - "User"
Cohesion: 0.24
Nodes (6): User, Entity, Table, UserEntity, InMemoryUserRepository, Override

### Community 34 - "LocalPasswordResetMailbox"
Cohesion: 0.21
Nodes (10): Component, Override, Profile, LocalPasswordResetMailbox, GetMapping, Profile, RequestMapping, RestController (+2 more)

### Community 35 - "JpaUserRepositoryAdapter"
Cohesion: 0.21
Nodes (4): Override, Repository, JpaUserRepositoryAdapter, SpringDataUserRepository

### Community 36 - "FollowController.java"
Cohesion: 0.17
Nodes (8): FollowController, Authentication, DeleteMapping, GetMapping, PostMapping, RequestMapping, ResponseEntity, RestController

### Community 38 - "AuthController.java"
Cohesion: 0.28
Nodes (9): AuthController, PostMapping, RequestMapping, ResponseEntity, RestController, LoginRequest, PasswordResetConfirmRequest, PasswordResetRequest (+1 more)

### Community 39 - "LibraryServiceTest.java"
Cohesion: 0.24
Nodes (4): InMemoryUserBookRepository, Override, Test, LibraryServiceTest

### Community 40 - "SecurityProperties"
Cohesion: 0.29
Nodes (7): ConfigurationProperties, Component, JwtEncoder, Override, JwtAccessTokenIssuer, SecurityProperties, Validated

### Community 41 - "UserRegistrationService"
Cohesion: 0.31
Nodes (6): Bean, Configuration, PasswordEncoder, UserApplicationConfiguration, PasswordEncoder, UserRegistrationService

### Community 42 - "ReadingSessionService.java"
Cohesion: 0.24
Nodes (3): Transactional, Test, ReadingConsistencyCalculatorTest

### Community 43 - "Yomora API Application Configuration"
Cohesion: 0.24
Nodes (10): Caffeine Cache, CORS Allowed Origins Policy, Flyway Migrations, JPA Schema Validation, JWT Security Configuration, Health and Info Management Endpoints, PostgreSQL Datasource, Sanitized Server Error Responses (+2 more)

### Community 44 - "AuthApiIntegrationTest.java"
Cohesion: 0.43
Nodes (6): AutoConfigureMockMvc, MockMvc, AuthApiIntegrationTest, Import, SpringBootTest, Test

### Community 47 - "ReviewAndNoteServiceTest.java"
Cohesion: 0.46
Nodes (3): CapturingPublisher, Test, ReviewAndNoteServiceTest

### Community 48 - "TestcontainersConfiguration.java"
Cohesion: 0.48
Nodes (5): PostgreSQLContainer, ServiceConnection, Bean, TestcontainersConfiguration, TestConfiguration

### Community 49 - "ReadingStatus"
Cohesion: 0.33
Nodes (6): ReadingStatus, ABANDONED, FINISHED, PAUSED, READING, WANT_TO_READ

### Community 50 - "YomoraApiApplicationTests.java"
Cohesion: 0.53
Nodes (4): Import, SpringBootTest, Test, YomoraApiApplicationTests

### Community 51 - "YomoraApiApplication"
Cohesion: 0.60
Nodes (3): ConfigurationPropertiesScan, SpringBootApplication, YomoraApiApplication

### Community 52 - "ApplicationConfiguration.java"
Cohesion: 0.60
Nodes (3): ApplicationConfiguration, Bean, Configuration

### Community 53 - "SocialRepository"
Cohesion: 0.17
Nodes (6): SocialRepository, Bean, Configuration, SocialConfiguration, Test, SocialServiceTest

### Community 55 - "ModularArchitectureTest.java"
Cohesion: 0.83
Nodes (3): AnalyzeClasses, ArchRule, ModularArchitectureTest

### Community 56 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **18 isolated node(s):** `WANT_TO_READ`, `READING`, `PAUSED`, `FINISHED`, `ABANDONED` (+13 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **9 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `CurrentUser` connect `CurrentUser` to `Shelf`, `BlockedUser`, `FollowController.java`, `LibraryService`, `ReadingGoal`, `UserRepository`, `PostController.java`, `UserController.java`, `.summary`, `.finish`, `LibraryController.java`, `ReviewController.java`?**
  _High betweenness centrality (0.213) - this node is a cross-community bridge._
- **Why does `UserRepository` connect `UserRepository` to `.register`, `User`, `JpaUserRepositoryAdapter`, `PasswordResetToken`, `UserRegistrationService`, `InMemoryUserRepository`, `InMemoryUserRepository`, `AuthenticationService`, `UserController.java`?**
  _High betweenness centrality (0.098) - this node is a cross-community bridge._
- **Why does `PasswordResetService` connect `PasswordResetToken` to `AuthenticationService`, `UserRepository`, `AuthController.java`?**
  _High betweenness centrality (0.076) - this node is a cross-community bridge._
- **What connects `WANT_TO_READ`, `READING`, `PAUSED` to the rest of the system?**
  _18 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Shelf` be split into smaller, more focused modules?**
  _Cohesion score 0.06630630630630631 - nodes in this community are weakly interconnected._
- **Should `BlockedUser` be split into smaller, more focused modules?**
  _Cohesion score 0.06050228310502283 - nodes in this community are weakly interconnected._
- **Should `BookCatalogRepository` be split into smaller, more focused modules?**
  _Cohesion score 0.06749482401656315 - nodes in this community are weakly interconnected._
# Graph Report - .  (2026-07-13)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1270 nodes · 2985 edges · 65 communities (61 shown, 4 thin omitted)
- Extraction: 92% EXTRACTED · 8% INFERRED · 0% AMBIGUOUS · INFERRED: 250 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `01834f94`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Shelf
- BlockedUser
- AuthenticationService
- GlobalExceptionHandler.java
- PasswordResetToken
- JpaRepository
- Review
- SocialService
- SecurityProperties
- ReadingStatus
- UserController.java
- PostType
- Note
- Post
- ReadingGoal
- .finish
- UserBook
- CurrentUser
- PostController.java
- BookCatalogRepository
- .update
- BookCandidate
- ReadingSession
- ReviewRepository
- JpaBookCatalogRepository
- UserRepository
- NoteRepository
- GoogleBooksProvider.java
- UserBookRepository
- ReadingGoalRepository
- User
- ReadingSessionRepository
- StatisticsService
- ReviewController.java
- PersistenceQueryIntegrationTest
- LocalPasswordResetMailbox
- JpaUserRepositoryAdapter
- NoteController.java
- FollowController.java
- Comment
- LibraryServiceTest.java
- BookCatalogController.java
- BookSearchResult
- BookWorkEntity
- ReadingSessionService.java
- Yomora API Application Configuration
- BookCatalogAggregatorTest.java
- JpaReadingGoalRepository
- AuthApiIntegrationTest.java
- InMemoryUserRepository
- InMemoryUserRepository
- ReviewAndNoteServiceTest.java
- TestcontainersConfiguration.java
- UserApplicationConfiguration.java
- YomoraApiApplicationTests.java
- YomoraApiApplication
- DailyReadingProjection
- ApplicationConfiguration.java
- SocialConfiguration.java
- ModularArchitectureTest.java
- gradlew
- TestYomoraApiApplication

## God Nodes (most connected - your core abstractions)
1. `User` - 43 edges
2. `Post` - 41 edges
3. `UserBook` - 39 edges
4. `CurrentUser` - 36 edges
5. `BookCatalogRepository` - 34 edges
6. `Shelf` - 34 edges
7. `BookSearchResult` - 33 edges
8. `SocialService` - 33 edges
9. `UserRepository` - 32 edges
10. `Review` - 31 edges

## Surprising Connections (you probably didn't know these)
- `BookCatalogController` --references--> `BookCatalogAggregator`  [EXTRACTED]
  src/main/java/com/yomora/catalog/web/BookCatalogController.java → src/main/java/com/yomora/catalog/application/BookCatalogAggregator.java
- `InMemoryCatalogRepository` --references--> `BookCandidate`  [EXTRACTED]
  src/test/java/com/yomora/catalog/application/BookCatalogAggregatorTest.java → src/main/java/com/yomora/catalog/domain/BookCandidate.java
- `JpaBookCatalogRepository` --implements--> `BookCatalogRepository`  [EXTRACTED]
  src/main/java/com/yomora/catalog/infrastructure/persistence/JpaBookCatalogRepository.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java
- `BookCatalogController` --references--> `BookCatalogRepository`  [EXTRACTED]
  src/main/java/com/yomora/catalog/web/BookCatalogController.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java
- `LibraryService` --references--> `BookCatalogRepository`  [EXTRACTED]
  src/main/java/com/yomora/library/application/LibraryService.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java

## Import Cycles
- None detected.

## Communities (65 total, 4 thin omitted)

### Community 0 - "Shelf"
Cohesion: 0.07
Nodes (29): Transactional, ShelfService, Shelf, ShelfRepository, Bean, Configuration, LibraryConfiguration, Override (+21 more)

### Community 1 - "BlockedUser"
Cohesion: 0.06
Nodes (30): Transactional, ModerationService, BlockedUser, ModerationRepository, Report, Bean, Configuration, ModerationConfiguration (+22 more)

### Community 2 - "AuthenticationService"
Cohesion: 0.07
Nodes (26): AccessTokenIssuer, AuthenticationService, PasswordEncoder, SecureRandom, Service, Transactional, TokenPair, RefreshToken (+18 more)

### Community 3 - "GlobalExceptionHandler.java"
Cohesion: 0.06
Nodes (20): ExceptionHandler, HttpStatus, MethodArgumentNotValidException, ProblemDetail, RestControllerAdvice, IdentityConflictException, InvalidCredentialsException, InvalidPasswordResetTokenException (+12 more)

### Community 4 - "PasswordResetToken"
Cohesion: 0.07
Nodes (24): PasswordResetNotifier, PasswordEncoder, SecureRandom, Service, Transactional, PasswordResetService, PasswordResetToken, PasswordResetTokenRepository (+16 more)

### Community 5 - "JpaRepository"
Cohesion: 0.08
Nodes (16): JpaRepository, PostEntity, SpringDataReportRepository, FollowEntity, Entity, Table, Override, JpaSocialRepository (+8 more)

### Community 6 - "Review"
Cohesion: 0.14
Nodes (10): Review, Override, Repository, JpaReviewRepository, Entity, Table, ReviewEntity, SpringDataReviewRepository (+2 more)

### Community 7 - "SocialService"
Cohesion: 0.16
Nodes (3): Transactional, SocialService, SocialRepository

### Community 8 - "SecurityProperties"
Cohesion: 0.13
Nodes (17): ConfigurationProperties, CorsConfigurationSource, HttpSecurity, JwtDecoder, SecretKey, SecurityFilterChain, Component, JwtEncoder (+9 more)

### Community 9 - "ReadingStatus"
Cohesion: 0.12
Nodes (13): ReadingStatus, ABANDONED, FINISHED, PAUSED, READING, WANT_TO_READ, Override, Repository (+5 more)

### Community 10 - "UserController.java"
Cohesion: 0.14
Nodes (15): ProfileMetrics, Service, Transactional, ProfileMetricsService, Authentication, DeleteMapping, GetMapping, PatchMapping (+7 more)

### Community 11 - "PostType"
Cohesion: 0.11
Nodes (16): Component, Override, SocialPublisherAdapter, PostType, NOTE, PROGRESS, QUOTE, RECOMMENDATION (+8 more)

### Community 12 - "Note"
Cohesion: 0.14
Nodes (9): Note, Override, Repository, JpaNoteRepository, Entity, Table, NoteEntity, SpringDataNoteRepository (+1 more)

### Community 13 - "Post"
Cohesion: 0.14
Nodes (5): Post, InMemorySocialRepository, Override, Test, SocialServiceTest

### Community 14 - "ReadingGoal"
Cohesion: 0.15
Nodes (13): PutMapping, Transactional, ReadingGoalService, ReadingGoal, Entity, Table, ReadingGoalEntity, GoalRequest (+5 more)

### Community 15 - ".finish"
Cohesion: 0.14
Nodes (13): Transactional, ReadingSessionService, ReadingSessionSummary, FinishRequest, Authentication, GetMapping, PatchMapping, PostMapping (+5 more)

### Community 16 - "UserBook"
Cohesion: 0.17
Nodes (7): UserBook, FixedCatalogRepository, InMemoryUserBookRepository, Override, Test, MutableClock, ReadingSessionServiceTest

### Community 17 - "CurrentUser"
Cohesion: 0.16
Nodes (14): AddBookRequest, Authentication, DeleteMapping, GetMapping, PatchMapping, PostMapping, RequestMapping, ResponseEntity (+6 more)

### Community 18 - "PostController.java"
Cohesion: 0.19
Nodes (12): CommentRequest, CreatePostRequest, EditPostRequest, Authentication, DeleteMapping, GetMapping, PatchMapping, PostMapping (+4 more)

### Community 19 - "BookCatalogRepository"
Cohesion: 0.17
Nodes (9): Cacheable, EnableCaching, FunctionalInterface, BookCatalogAggregator, BookCatalogRepository, BookProvider, CatalogConfiguration, Bean (+1 more)

### Community 20 - ".update"
Cohesion: 0.17
Nodes (7): UpdateProfileCommand, Service, Transactional, UserProfileService, UserNotFoundException, Test, UserProfileServiceTest

### Community 21 - "BookCandidate"
Cohesion: 0.18
Nodes (10): BookCandidate, BookProviderQuery, Override, Builder, Component, Override, RestClient, OpenLibraryDocument (+2 more)

### Community 22 - "ReadingSession"
Cohesion: 0.17
Nodes (6): ReadingSession, Override, Entity, Table, ReadingSessionEntity, InMemoryReadingSessionRepository

### Community 23 - "ReviewRepository"
Cohesion: 0.19
Nodes (3): Transactional, ReviewService, ReviewRepository

### Community 24 - "JpaBookCatalogRepository"
Cohesion: 0.20
Nodes (9): BookEditionEntity, Pageable, Override, Repository, Transactional, JpaBookCatalogRepository, Query, SpringDataBookEditionRepository (+1 more)

### Community 25 - "UserRepository"
Cohesion: 0.20
Nodes (7): RegisterCommand, PasswordEncoder, UserRegistrationService, UserRepository, BCryptPasswordEncoder, Test, UserRegistrationServiceTest

### Community 26 - "NoteRepository"
Cohesion: 0.20
Nodes (7): Transactional, NoteService, SocialPublisher, NoteRepository, Bean, Configuration, ReviewConfiguration

### Community 27 - "GoogleBooksProvider.java"
Cohesion: 0.18
Nodes (10): BookProviderUnavailableException, GoogleBooksProvider, GoogleBooksResponse, GoogleImageLinks, GoogleIndustryIdentifier, GoogleVolume, GoogleVolumeInfo, Builder (+2 more)

### Community 28 - "UserBookRepository"
Cohesion: 0.20
Nodes (3): Transactional, LibraryService, UserBookRepository

### Community 29 - "ReadingGoalRepository"
Cohesion: 0.20
Nodes (6): ConsistencySummary, ReadingConsistencyCalculator, ReadingGoalRepository, Bean, Configuration, ReadingConfiguration

### Community 30 - "User"
Cohesion: 0.23
Nodes (6): User, Entity, Table, UserEntity, InMemoryUserRepository, Override

### Community 31 - "ReadingSessionRepository"
Cohesion: 0.18
Nodes (6): DailyReading, ReadingSessionRepository, Repository, JpaReadingSessionRepository, Query, SpringDataReadingSessionRepository

### Community 32 - "StatisticsService"
Cohesion: 0.19
Nodes (9): StatisticsService, StatisticsSummary, Authentication, GetMapping, RequestMapping, RestController, StatisticsController, Test (+1 more)

### Community 33 - "ReviewController.java"
Cohesion: 0.21
Nodes (11): Authentication, DeleteMapping, GetMapping, PatchMapping, PostMapping, RequestMapping, ResponseEntity, RestController (+3 more)

### Community 34 - "PersistenceQueryIntegrationTest"
Cohesion: 0.25
Nodes (9): BookCatalogRepository, Import, JdbcTemplate, Post, SocialRepository, SpringBootTest, PersistenceQueryIntegrationTest, Test (+1 more)

### Community 35 - "LocalPasswordResetMailbox"
Cohesion: 0.21
Nodes (10): Component, Override, Profile, LocalPasswordResetMailbox, GetMapping, Profile, RequestMapping, RestController (+2 more)

### Community 36 - "JpaUserRepositoryAdapter"
Cohesion: 0.22
Nodes (4): Override, Repository, JpaUserRepositoryAdapter, SpringDataUserRepository

### Community 37 - "NoteController.java"
Cohesion: 0.26
Nodes (9): Authentication, DeleteMapping, GetMapping, PostMapping, RequestMapping, ResponseEntity, RestController, NoteController (+1 more)

### Community 38 - "FollowController.java"
Cohesion: 0.25
Nodes (8): FollowController, Authentication, DeleteMapping, GetMapping, PostMapping, RequestMapping, ResponseEntity, RestController

### Community 39 - "Comment"
Cohesion: 0.24
Nodes (5): Comment, CommentEntity, Entity, Table, Repository

### Community 40 - "LibraryServiceTest.java"
Cohesion: 0.24
Nodes (4): InMemoryUserBookRepository, Override, Test, LibraryServiceTest

### Community 41 - "BookCatalogController.java"
Cohesion: 0.26
Nodes (7): BookCatalogController, GetMapping, PostMapping, RequestMapping, ResponseEntity, RestController, ManualBookRequest

### Community 42 - "BookSearchResult"
Cohesion: 0.27
Nodes (3): BookSearchResult, Override, FixedCatalogRepository

### Community 43 - "BookWorkEntity"
Cohesion: 0.27
Nodes (6): BookEditionEntity, Entity, Table, BookWorkEntity, Entity, Table

### Community 44 - "ReadingSessionService.java"
Cohesion: 0.24
Nodes (3): Transactional, Test, ReadingConsistencyCalculatorTest

### Community 45 - "Yomora API Application Configuration"
Cohesion: 0.24
Nodes (10): Caffeine Cache, CORS Allowed Origins Policy, Flyway Migrations, JPA Schema Validation, JWT Security Configuration, Health and Info Management Endpoints, PostgreSQL Datasource, Sanitized Server Error Responses (+2 more)

### Community 46 - "BookCatalogAggregatorTest.java"
Cohesion: 0.42
Nodes (4): SearchBooksQuery, BookCatalogAggregatorTest, InMemoryCatalogRepository, Test

### Community 47 - "JpaReadingGoalRepository"
Cohesion: 0.31
Nodes (4): Override, Repository, JpaReadingGoalRepository, SpringDataReadingGoalRepository

### Community 48 - "AuthApiIntegrationTest.java"
Cohesion: 0.43
Nodes (6): AutoConfigureMockMvc, MockMvc, AuthApiIntegrationTest, Import, SpringBootTest, Test

### Community 51 - "ReviewAndNoteServiceTest.java"
Cohesion: 0.46
Nodes (3): CapturingPublisher, Test, ReviewAndNoteServiceTest

### Community 52 - "TestcontainersConfiguration.java"
Cohesion: 0.48
Nodes (5): PostgreSQLContainer, ServiceConnection, Bean, TestcontainersConfiguration, TestConfiguration

### Community 53 - "UserApplicationConfiguration.java"
Cohesion: 0.53
Nodes (4): Bean, Configuration, PasswordEncoder, UserApplicationConfiguration

### Community 54 - "YomoraApiApplicationTests.java"
Cohesion: 0.53
Nodes (4): Import, SpringBootTest, Test, YomoraApiApplicationTests

### Community 55 - "YomoraApiApplication"
Cohesion: 0.60
Nodes (3): ConfigurationPropertiesScan, SpringBootApplication, YomoraApiApplication

### Community 57 - "ApplicationConfiguration.java"
Cohesion: 0.60
Nodes (3): ApplicationConfiguration, Bean, Configuration

### Community 58 - "SocialConfiguration.java"
Cohesion: 0.60
Nodes (3): Bean, Configuration, SocialConfiguration

### Community 59 - "ModularArchitectureTest.java"
Cohesion: 0.83
Nodes (3): AnalyzeClasses, ArchRule, ModularArchitectureTest

### Community 60 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **18 isolated node(s):** `WANT_TO_READ`, `READING`, `PAUSED`, `FINISHED`, `ABANDONED` (+13 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **4 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `CurrentUser` connect `CurrentUser` to `Shelf`, `BlockedUser`, `StatisticsService`, `ReviewController.java`, `NoteController.java`, `FollowController.java`, `UserController.java`, `ReadingGoal`, `.finish`, `PostController.java`, `UserBookRepository`?**
  _High betweenness centrality (0.243) - this node is a cross-community bridge._
- **Why does `User` connect `User` to `AuthenticationService`, `PasswordResetToken`, `JpaUserRepositoryAdapter`, `SecurityProperties`, `UserController.java`, `InMemoryUserRepository`, `InMemoryUserRepository`, `.update`, `UserRepository`?**
  _High betweenness centrality (0.086) - this node is a cross-community bridge._
- **Why does `UserRepository` connect `UserRepository` to `AuthenticationService`, `PasswordResetToken`, `JpaUserRepositoryAdapter`, `UserController.java`, `InMemoryUserRepository`, `InMemoryUserRepository`, `.update`, `UserApplicationConfiguration.java`, `User`?**
  _High betweenness centrality (0.083) - this node is a cross-community bridge._
- **What connects `WANT_TO_READ`, `READING`, `PAUSED` to the rest of the system?**
  _18 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Shelf` be split into smaller, more focused modules?**
  _Cohesion score 0.06630630630630631 - nodes in this community are weakly interconnected._
- **Should `BlockedUser` be split into smaller, more focused modules?**
  _Cohesion score 0.06237424547283702 - nodes in this community are weakly interconnected._
- **Should `AuthenticationService` be split into smaller, more focused modules?**
  _Cohesion score 0.06912442396313365 - nodes in this community are weakly interconnected._
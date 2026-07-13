# Graph Report - .  (2026-07-13)

## Corpus Check
- Corpus is ~19,073 words - fits in a single context window. You may not need a graph.

## Summary
- 1252 nodes · 2952 edges · 62 communities (59 shown, 3 thin omitted)
- Extraction: 92% EXTRACTED · 8% INFERRED · 0% AMBIGUOUS · INFERRED: 250 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Shelves and Collections
- User Moderation
- Moderation Persistence
- Token Issuance
- API Error Handling
- Review Persistence
- Security Configuration
- Reading Status
- Reading Goals
- Note Persistence
- Password Reset Ports
- Profile Metrics
- Profile Updates
- Reset Token Domain
- User Persistence
- Library Service
- Feed Queries
- Catalog Orchestration
- Catalog Domain
- Social Service
- Post HTTP API
- Session Service Tests
- Reading Sessions
- Session Summaries
- Social Publishing
- User Profile API
- Notes Service
- Comments Domain
- Library Catalog Bridge
- Reading Projections
- Catalog Persistence
- Goal HTTP API
- Google Books Provider
- Reading Statistics
- Review Service
- Library HTTP API
- Review HTTP API
- Provider Failover
- User Registration
- Local Reset Mailbox
- User Repository Adapter
- Password Reset Tests
- Search Query Mapping
- Note HTTP API
- Follow HTTP API
- Edition Persistence
- Runtime Configuration
- Authentication Integration Tests
- Review Note Tests
- Testcontainers Setup
- Identity Configuration
- Application Context Tests
- Spring Boot Entry
- Consistency Unit Tests
- Shared Clock Configuration
- Social Configuration
- Architecture Rules
- Gradle Wrapper
- Test Application Entry

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
- `BookCatalogAggregator` --references--> `BookCatalogRepository`  [EXTRACTED]
  src/main/java/com/yomora/catalog/application/BookCatalogAggregator.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java
- `BookCatalogController` --references--> `BookCatalogAggregator`  [EXTRACTED]
  src/main/java/com/yomora/catalog/web/BookCatalogController.java → src/main/java/com/yomora/catalog/application/BookCatalogAggregator.java
- `JpaBookCatalogRepository` --implements--> `BookCatalogRepository`  [EXTRACTED]
  src/main/java/com/yomora/catalog/infrastructure/persistence/JpaBookCatalogRepository.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java
- `BookCatalogController` --references--> `BookCatalogRepository`  [EXTRACTED]
  src/main/java/com/yomora/catalog/web/BookCatalogController.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java
- `ReadingSessionService` --references--> `BookCatalogRepository`  [EXTRACTED]
  src/main/java/com/yomora/reading/application/ReadingSessionService.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java

## Import Cycles
- None detected.

## Communities (62 total, 3 thin omitted)

### Community 0 - "Shelves and Collections"
Cohesion: 0.07
Nodes (26): Transactional, ShelfService, Shelf, ShelfRepository, Override, Repository, JpaShelfRepository, Entity (+18 more)

### Community 1 - "User Moderation"
Cohesion: 0.07
Nodes (29): Transactional, ModerationService, BlockedUser, ModerationRepository, Report, Bean, Configuration, ModerationConfiguration (+21 more)

### Community 2 - "Moderation Persistence"
Cohesion: 0.06
Nodes (23): JpaRepository, SpringDataBlockedUserRepository, SpringDataReportRepository, CommentEntity, Entity, Table, FollowEntity, Entity (+15 more)

### Community 3 - "Token Issuance"
Cohesion: 0.07
Nodes (26): AccessTokenIssuer, AuthenticationService, PasswordEncoder, SecureRandom, Service, Transactional, TokenPair, RefreshToken (+18 more)

### Community 4 - "API Error Handling"
Cohesion: 0.06
Nodes (20): ExceptionHandler, HttpStatus, MethodArgumentNotValidException, ProblemDetail, RestControllerAdvice, IdentityConflictException, InvalidCredentialsException, InvalidPasswordResetTokenException (+12 more)

### Community 5 - "Review Persistence"
Cohesion: 0.14
Nodes (10): Review, Override, Repository, JpaReviewRepository, Entity, Table, ReviewEntity, SpringDataReviewRepository (+2 more)

### Community 6 - "Security Configuration"
Cohesion: 0.13
Nodes (17): ConfigurationProperties, CorsConfigurationSource, HttpSecurity, JwtDecoder, SecretKey, SecurityFilterChain, Component, JwtEncoder (+9 more)

### Community 7 - "Reading Status"
Cohesion: 0.12
Nodes (13): ReadingStatus, ABANDONED, FINISHED, PAUSED, READING, WANT_TO_READ, Override, Repository (+5 more)

### Community 8 - "Reading Goals"
Cohesion: 0.13
Nodes (11): Transactional, ReadingGoalService, ReadingGoal, ReadingGoalRepository, Override, Repository, JpaReadingGoalRepository, Entity (+3 more)

### Community 9 - "Note Persistence"
Cohesion: 0.14
Nodes (9): Note, Override, Repository, JpaNoteRepository, Entity, Table, NoteEntity, SpringDataNoteRepository (+1 more)

### Community 10 - "Password Reset Ports"
Cohesion: 0.12
Nodes (10): PasswordResetNotifier, PasswordEncoder, SecureRandom, Service, Transactional, PasswordResetService, DiscardingPasswordResetNotifier, Component (+2 more)

### Community 11 - "Profile Metrics"
Cohesion: 0.16
Nodes (10): Service, Transactional, ProfileMetricsService, ConsistencySummary, ReadingConsistencyCalculator, StatisticsService, ReadingSessionRepository, Bean (+2 more)

### Community 12 - "Profile Updates"
Cohesion: 0.16
Nodes (8): UpdateProfileCommand, Service, Transactional, UserProfileService, UserRepository, UserNotFoundException, Test, UserProfileServiceTest

### Community 13 - "Reset Token Domain"
Cohesion: 0.14
Nodes (10): PasswordResetToken, PasswordResetTokenRepository, Override, Repository, JpaPasswordResetTokenRepositoryAdapter, Entity, Table, PasswordResetTokenEntity (+2 more)

### Community 14 - "User Persistence"
Cohesion: 0.16
Nodes (8): User, Entity, Table, UserEntity, InMemoryUserRepository, Override, InMemoryUserRepository, Override

### Community 15 - "Library Service"
Cohesion: 0.17
Nodes (6): Transactional, UserBook, InMemoryUserBookRepository, Override, Test, LibraryServiceTest

### Community 16 - "Feed Queries"
Cohesion: 0.17
Nodes (3): Post, InMemorySocialRepository, Override

### Community 17 - "Catalog Orchestration"
Cohesion: 0.17
Nodes (9): Cacheable, EnableCaching, FunctionalInterface, BookCatalogAggregator, BookProvider, BookProviderQuery, CatalogConfiguration, Bean (+1 more)

### Community 18 - "Catalog Domain"
Cohesion: 0.16
Nodes (9): BookSearchResult, BookCatalogController, GetMapping, PostMapping, RequestMapping, ResponseEntity, RestController, ManualBookRequest (+1 more)

### Community 20 - "Post HTTP API"
Cohesion: 0.21
Nodes (12): CommentRequest, CreatePostRequest, EditPostRequest, Authentication, DeleteMapping, GetMapping, PatchMapping, PostMapping (+4 more)

### Community 21 - "Session Service Tests"
Cohesion: 0.16
Nodes (6): FixedCatalogRepository, InMemoryUserBookRepository, Override, Test, MutableClock, ReadingSessionServiceTest

### Community 22 - "Reading Sessions"
Cohesion: 0.15
Nodes (6): Transactional, ReadingSession, Entity, Table, ReadingSessionEntity, InMemoryReadingSessionRepository

### Community 23 - "Session Summaries"
Cohesion: 0.18
Nodes (12): ReadingSessionService, ReadingSessionSummary, FinishRequest, Authentication, GetMapping, PatchMapping, PostMapping, RequestMapping (+4 more)

### Community 24 - "Social Publishing"
Cohesion: 0.13
Nodes (13): Component, Override, SocialPublisherAdapter, PostType, NOTE, PROGRESS, QUOTE, RECOMMENDATION (+5 more)

### Community 25 - "User Profile API"
Cohesion: 0.20
Nodes (12): ProfileMetrics, Authentication, DeleteMapping, GetMapping, PatchMapping, RequestMapping, ResponseEntity, RestController (+4 more)

### Community 26 - "Notes Service"
Cohesion: 0.20
Nodes (7): Transactional, NoteService, SocialPublisher, NoteRepository, Bean, Configuration, ReviewConfiguration

### Community 27 - "Comments Domain"
Cohesion: 0.14
Nodes (4): Comment, SocialRepository, Test, SocialServiceTest

### Community 28 - "Library Catalog Bridge"
Cohesion: 0.19
Nodes (6): BookCatalogRepository, LibraryService, UserBookRepository, Bean, Configuration, LibraryConfiguration

### Community 29 - "Reading Projections"
Cohesion: 0.15
Nodes (6): DailyReadingProjection, Override, Repository, JpaReadingSessionRepository, Query, SpringDataReadingSessionRepository

### Community 30 - "Catalog Persistence"
Cohesion: 0.20
Nodes (8): Pageable, Override, Repository, Transactional, JpaBookCatalogRepository, Query, SpringDataBookEditionRepository, SpringDataBookWorkRepository

### Community 31 - "Goal HTTP API"
Cohesion: 0.18
Nodes (10): PutMapping, GoalRequest, Authentication, GetMapping, RequestMapping, RestController, ReadingGoalController, CurrentUser (+2 more)

### Community 32 - "Google Books Provider"
Cohesion: 0.20
Nodes (10): GoogleBooksProvider, GoogleBooksResponse, GoogleImageLinks, GoogleIndustryIdentifier, GoogleVolume, GoogleVolumeInfo, Builder, Component (+2 more)

### Community 33 - "Reading Statistics"
Cohesion: 0.17
Nodes (10): DailyReading, Transactional, StatisticsSummary, Authentication, GetMapping, RequestMapping, RestController, StatisticsController (+2 more)

### Community 34 - "Review Service"
Cohesion: 0.22
Nodes (3): Transactional, ReviewService, ReviewRepository

### Community 35 - "Library HTTP API"
Cohesion: 0.22
Nodes (11): AddBookRequest, Authentication, DeleteMapping, GetMapping, PatchMapping, PostMapping, RequestMapping, ResponseEntity (+3 more)

### Community 36 - "Review HTTP API"
Cohesion: 0.21
Nodes (11): Authentication, DeleteMapping, GetMapping, PatchMapping, PostMapping, RequestMapping, ResponseEntity, RestController (+3 more)

### Community 37 - "Provider Failover"
Cohesion: 0.19
Nodes (8): BookProviderUnavailableException, Builder, Component, Override, RestClient, OpenLibraryDocument, OpenLibraryProvider, OpenLibraryResponse

### Community 38 - "User Registration"
Cohesion: 0.21
Nodes (6): RegisterCommand, PasswordEncoder, UserRegistrationService, BCryptPasswordEncoder, Test, UserRegistrationServiceTest

### Community 39 - "Local Reset Mailbox"
Cohesion: 0.21
Nodes (10): Component, Override, Profile, LocalPasswordResetMailbox, GetMapping, Profile, RequestMapping, RestController (+2 more)

### Community 40 - "User Repository Adapter"
Cohesion: 0.21
Nodes (4): Override, Repository, JpaUserRepositoryAdapter, SpringDataUserRepository

### Community 41 - "Password Reset Tests"
Cohesion: 0.23
Nodes (6): CapturingNotifier, InMemoryUserRepository, BCryptPasswordEncoder, Override, Test, PasswordResetServiceTest

### Community 42 - "Search Query Mapping"
Cohesion: 0.27
Nodes (6): SearchBooksQuery, BookCandidate, BookCatalogAggregatorTest, InMemoryCatalogRepository, Override, Test

### Community 43 - "Note HTTP API"
Cohesion: 0.26
Nodes (9): Authentication, DeleteMapping, GetMapping, PostMapping, RequestMapping, ResponseEntity, RestController, NoteController (+1 more)

### Community 44 - "Follow HTTP API"
Cohesion: 0.25
Nodes (8): FollowController, Authentication, DeleteMapping, GetMapping, PostMapping, RequestMapping, ResponseEntity, RestController

### Community 45 - "Edition Persistence"
Cohesion: 0.27
Nodes (6): BookEditionEntity, Entity, Table, BookWorkEntity, Entity, Table

### Community 46 - "Runtime Configuration"
Cohesion: 0.24
Nodes (10): Caffeine Cache, CORS Allowed Origins Policy, Flyway Migrations, JPA Schema Validation, JWT Security Configuration, Health and Info Management Endpoints, PostgreSQL Datasource, Sanitized Server Error Responses (+2 more)

### Community 47 - "Authentication Integration Tests"
Cohesion: 0.43
Nodes (6): AutoConfigureMockMvc, MockMvc, AuthApiIntegrationTest, Import, SpringBootTest, Test

### Community 48 - "Review Note Tests"
Cohesion: 0.46
Nodes (3): CapturingPublisher, Test, ReviewAndNoteServiceTest

### Community 49 - "Testcontainers Setup"
Cohesion: 0.48
Nodes (5): PostgreSQLContainer, ServiceConnection, Bean, TestcontainersConfiguration, TestConfiguration

### Community 50 - "Identity Configuration"
Cohesion: 0.53
Nodes (4): Bean, Configuration, PasswordEncoder, UserApplicationConfiguration

### Community 51 - "Application Context Tests"
Cohesion: 0.53
Nodes (4): Import, SpringBootTest, Test, YomoraApiApplicationTests

### Community 52 - "Spring Boot Entry"
Cohesion: 0.60
Nodes (3): ConfigurationPropertiesScan, SpringBootApplication, YomoraApiApplication

### Community 54 - "Shared Clock Configuration"
Cohesion: 0.60
Nodes (3): ApplicationConfiguration, Bean, Configuration

### Community 55 - "Social Configuration"
Cohesion: 0.60
Nodes (3): Bean, Configuration, SocialConfiguration

### Community 56 - "Architecture Rules"
Cohesion: 0.83
Nodes (3): AnalyzeClasses, ArchRule, ModularArchitectureTest

### Community 57 - "Gradle Wrapper"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **18 isolated node(s):** `WANT_TO_READ`, `READING`, `PAUSED`, `FINISHED`, `ABANDONED` (+13 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **3 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `CurrentUser` connect `Goal HTTP API` to `Shelves and Collections`, `User Moderation`, `Reading Statistics`, `Library HTTP API`, `Review HTTP API`, `Note HTTP API`, `Profile Updates`, `Follow HTTP API`, `Post HTTP API`, `Session Summaries`, `User Profile API`?**
  _High betweenness centrality (0.243) - this node is a cross-community bridge._
- **Why does `UserRepository` connect `Profile Updates` to `Token Issuance`, `User Registration`, `User Repository Adapter`, `Password Reset Tests`, `Password Reset Ports`, `User Persistence`, `Identity Configuration`, `User Profile API`?**
  _High betweenness centrality (0.088) - this node is a cross-community bridge._
- **Why does `User` connect `User Persistence` to `Token Issuance`, `User Registration`, `Security Configuration`, `User Repository Adapter`, `Password Reset Tests`, `Password Reset Ports`, `Profile Updates`, `User Profile API`?**
  _High betweenness centrality (0.083) - this node is a cross-community bridge._
- **What connects `WANT_TO_READ`, `READING`, `PAUSED` to the rest of the system?**
  _18 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Shelves and Collections` be split into smaller, more focused modules?**
  _Cohesion score 0.07204968944099378 - nodes in this community are weakly interconnected._
- **Should `User Moderation` be split into smaller, more focused modules?**
  _Cohesion score 0.06540825285338016 - nodes in this community are weakly interconnected._
- **Should `Moderation Persistence` be split into smaller, more focused modules?**
  _Cohesion score 0.05920745920745921 - nodes in this community are weakly interconnected._
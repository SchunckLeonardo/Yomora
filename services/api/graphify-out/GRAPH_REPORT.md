# Graph Report - .  (2026-07-15)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1327 nodes · 1858 edges · 325 communities (57 shown, 268 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 266 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `e297acea`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- BookCandidate
- AuthenticationService
- ShelfRepository
- ModerationRepository
- BookProviderQuery
- GlobalExceptionHandler
- BookCandidate.java
- BookCatalogRepository
- PasswordResetTokenRepository
- SocialService
- PersistenceQueryIntegrationTest
- JpaReadingSessionRepository
- .update
- JpaUserRepositoryAdapter
- JpaUserBookRepository
- ReviewRepository
- JpaReviewRepository
- JpaNoteRepository
- PostController
- JpaSocialRepository
- InMemorySocialRepository
- .add
- .combinesSocialAndReadingMetricsForAProfile
- InMemoryUserBookRepository
- SecurityConfiguration
- JpaRepository
- SpringDataPostRepository
- .finish
- ProfileMetricsService
- InMemoryUserRepository
- .calculate
- InMemoryNoteRepository
- UserRepository
- JpaPasswordResetTokenRepositoryAdapter
- SocialRepository
- JpaReadingGoalRepository
- NoteRepository
- BookEditionEntity
- Yomora API Application Configuration
- SocialPublisher
- ReadingGoalRepository
- FollowController
- BookProvider
- LocalPasswordResetMailbox
- ReviewController
- CommentEntity
- InMemoryUserRepository
- ReadingGoalController
- ReadingStatus
- NoteController
- PostType
- PasswordResetServiceTest.java
- InMemoryUserRepository
- TestcontainersConfiguration.java
- BookWorkEntity.java
- InMemoryReviewRepository
- AuthApiIntegrationTest
- YomoraApiApplication
- Q: Existe uma API de livros mais completa para buscas como Paul Washer?
- Q: Vamos seguir nisso então, teste bem porque antes a gente teve que trocar pt por por, porque era o formato aceito. Então veja o que funciona.
- Visibility
- ModularArchitectureTest.java
- SecurityProperties.java
- gradlew
- BookProviderUnavailableException
- InvalidCredentialsException
- InvalidPasswordResetTokenException
- InvalidRefreshTokenException
- InvalidBookProgressException
- LibraryEntryNotFoundException
- ShelfNotFoundException
- ReadingSessionNotFoundException
- ReadingSession
- NoteNotFoundException
- ReviewNotFoundException
- ApplicationConfiguration
- CurrentUser
- TestYomoraApiApplication
- YomoraApiApplicationTests
- ProfileMetrics.java
- TokenPair.java
- Shelf.java
- BlockedUser
- Report.java
- ReadingSessionSummary.java
- Note.java
- Review.java
- Comment
- Repository
- Transactional
- GetMapping
- PostMapping
- RequestMapping
- ResponseEntity
- RestController
- Bean
- Configuration
- Entity
- Table
- PasswordEncoder
- SecureRandom
- Service
- Transactional
- PasswordEncoder
- SecureRandom
- Service
- Transactional
- Service
- Transactional
- Bean
- Configuration
- PasswordEncoder
- Service
- Transactional
- PasswordEncoder
- Component
- Override
- Profile
- Component
- Override
- Profile
- Override
- Repository
- Override
- Repository
- Override
- Repository
- Entity
- Table
- Entity
- Table
- Entity
- Table
- Component
- JwtEncoder
- Override
- PostMapping
- RequestMapping
- ResponseEntity
- RestController
- GetMapping
- Profile
- RequestMapping
- RestController
- Authentication
- DeleteMapping
- GetMapping
- PatchMapping
- RequestMapping
- ResponseEntity
- RestController
- Transactional
- Transactional
- Bean
- Configuration
- Override
- Repository
- Override
- Repository
- Entity
- Table
- Entity
- Table
- Authentication
- DeleteMapping
- GetMapping
- PatchMapping
- PostMapping
- RequestMapping
- ResponseEntity
- RestController
- Authentication
- DeleteMapping
- GetMapping
- PatchMapping
- PostMapping
- RequestMapping
- ResponseEntity
- RestController
- Transactional
- Bean
- Configuration
- Entity
- Table
- Override
- Repository
- Entity
- Table
- Authentication
- DeleteMapping
- PostMapping
- RequestMapping
- ResponseEntity
- RestController
- Transactional
- Transactional
- Transactional
- Bean
- Configuration
- Override
- Repository
- Override
- Repository
- Entity
- Table
- Entity
- Table
- Query
- Authentication
- GetMapping
- RequestMapping
- RestController
- Authentication
- GetMapping
- PatchMapping
- PostMapping
- RequestMapping
- ResponseEntity
- RestController
- Authentication
- GetMapping
- RequestMapping
- RestController
- Transactional
- Transactional
- Bean
- Configuration
- Override
- Repository
- Override
- Repository
- Entity
- Table
- Entity
- Table
- Component
- Override
- Authentication
- DeleteMapping
- GetMapping
- PostMapping
- RequestMapping
- ResponseEntity
- RestController
- Authentication
- DeleteMapping
- GetMapping
- PatchMapping
- PostMapping
- RequestMapping
- ResponseEntity
- RestController
- Bean
- Configuration
- Authentication
- Component
- Bean
- Configuration
- JwtEncoder
- PasswordEncoder
- Transactional
- Bean
- Configuration
- Entity
- Table
- Entity
- Table
- Override
- Repository
- Entity
- Table
- Entity
- Table
- Authentication
- DeleteMapping
- GetMapping
- PostMapping
- RequestMapping
- ResponseEntity
- RestController
- Authentication
- DeleteMapping
- GetMapping
- PatchMapping
- PostMapping
- RequestMapping
- ResponseEntity
- RestController
- Import
- SpringBootTest
- Test
- BCryptPasswordEncoder
- InMemoryUserRepository
- Override
- Test
- Test
- InMemoryUserRepository
- Override
- Test
- BCryptPasswordEncoder
- InMemoryUserRepository
- Override
- Test
- Override
- Test
- Override
- Test
- Override
- Test
- Test
- Override
- Test
- Override
- Test
- Override
- Test
- Bean
- Import
- SpringBootTest
- Test

## God Nodes (most connected - your core abstractions)
1. `UserRepository` - 32 edges
2. `SocialRepository` - 29 edges
3. `BookCatalogRepository` - 26 edges
4. `UserBookRepository` - 26 edges
5. `ReadingSessionRepository` - 22 edges
6. `SocialService` - 22 edges
7. `BookCandidate` - 22 edges
8. `JpaSocialRepository` - 21 edges
9. `ReadingGoalRepository` - 18 edges
10. `InMemorySocialRepository` - 17 edges

## Surprising Connections (you probably didn't know these)
- `PersistenceQueryIntegrationTest` --references--> `BookCatalogRepository`  [EXTRACTED]
  src/test/java/com/yomora/PersistenceQueryIntegrationTest.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java
- `FixedCatalogRepository` --implements--> `BookCatalogRepository`  [EXTRACTED]
  src/test/java/com/yomora/library/application/LibraryServiceTest.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java
- `FixedCatalogRepository` --implements--> `BookCatalogRepository`  [EXTRACTED]
  src/test/java/com/yomora/reading/application/ReadingSessionServiceTest.java → src/main/java/com/yomora/catalog/domain/BookCatalogRepository.java
- `AuthenticationService` --references--> `UserRepository`  [EXTRACTED]
  src/main/java/com/yomora/identity/application/AuthenticationService.java → src/main/java/com/yomora/identity/domain/UserRepository.java
- `LocalPasswordResetMailbox` --implements--> `PasswordResetNotifier`  [EXTRACTED]
  src/main/java/com/yomora/identity/infrastructure/notification/LocalPasswordResetMailbox.java → src/main/java/com/yomora/identity/application/PasswordResetNotifier.java

## Import Cycles
- None detected.

## Communities (325 total, 268 thin omitted)

### Community 0 - "BookCandidate"
Cohesion: 0.07
Nodes (32): BookCatalogRepository, Cacheable, GetMapping, PostMapping, Repository, RequestMapping, ResponseEntity, RestController (+24 more)

### Community 1 - "AuthenticationService"
Cohesion: 0.06
Nodes (14): AccessTokenIssuer, AuthenticationService, RefreshToken, RefreshTokenRepository, JpaRefreshTokenRepositoryAdapter, RefreshTokenEntity, SpringDataRefreshTokenRepository, JwtAccessTokenIssuer (+6 more)

### Community 2 - "ShelfRepository"
Cohesion: 0.06
Nodes (10): ShelfService, ShelfRepository, JpaShelfRepository, ShelfEntity, SpringDataShelfRepository, ShelfBookRequest, ShelfController, ShelfRequest (+2 more)

### Community 3 - "ModerationRepository"
Cohesion: 0.06
Nodes (12): ModerationService, ModerationRepository, ModerationConfiguration, BlockedUserEntity, JpaModerationRepository, ReportEntity, SpringDataBlockedUserRepository, SpringDataReportRepository (+4 more)

### Community 4 - "BookProviderQuery"
Cohesion: 0.09
Nodes (23): BookProvider, BookProviderQuery, GoogleBooksProvider, GoogleBooksResponse, GoogleImageLinks, GoogleIndustryIdentifier, GoogleVolume, GoogleVolumeInfo (+15 more)

### Community 5 - "GlobalExceptionHandler"
Cohesion: 0.10
Nodes (14): ExceptionHandler, HttpStatus, MethodArgumentNotValidException, ProblemDetail, RestControllerAdvice, IdentityConflictException, LibraryConflictException, FinishRequest (+6 more)

### Community 6 - "BookCandidate.java"
Cohesion: 0.08
Nodes (6): BookSearchResult, FixedCatalogRepository, InMemoryReadingSessionRepository, InMemoryUserBookRepository, MutableClock, ReadingSessionServiceTest

### Community 7 - "BookCatalogRepository"
Cohesion: 0.14
Nodes (8): BookCatalogRepository, LibraryService, UserBookRepository, LibraryConfiguration, ReadingSessionService, StatisticsService, ReadingSessionRepository, ReadingConfiguration

### Community 8 - "PasswordResetTokenRepository"
Cohesion: 0.10
Nodes (6): PasswordResetNotifier, PasswordResetService, PasswordResetToken, PasswordResetTokenRepository, User, DiscardingPasswordResetNotifier

### Community 9 - "SocialService"
Cohesion: 0.13
Nodes (3): SocialService, EditPostRequest, SocialServiceTest

### Community 10 - "PersistenceQueryIntegrationTest"
Cohesion: 0.17
Nodes (8): EntityManager, JdbcTemplate, Import, SpringBootTest, Test, Transactional, PersistenceQueryIntegrationTest, Post

### Community 11 - "JpaReadingSessionRepository"
Cohesion: 0.13
Nodes (4): DailyReadingProjection, JpaReadingSessionRepository, ReadingSessionEntity, SpringDataReadingSessionRepository

### Community 12 - ".update"
Cohesion: 0.17
Nodes (4): UpdateProfileCommand, UserProfileService, UserNotFoundException, UserProfileServiceTest

### Community 13 - "JpaUserRepositoryAdapter"
Cohesion: 0.15
Nodes (3): JpaUserRepositoryAdapter, SpringDataUserRepository, UserEntity

### Community 14 - "JpaUserBookRepository"
Cohesion: 0.16
Nodes (3): JpaUserBookRepository, SpringDataUserBookRepository, UserBookEntity

### Community 16 - "JpaReviewRepository"
Cohesion: 0.17
Nodes (3): JpaReviewRepository, ReviewEntity, SpringDataReviewRepository

### Community 17 - "JpaNoteRepository"
Cohesion: 0.18
Nodes (3): JpaNoteRepository, NoteEntity, SpringDataNoteRepository

### Community 18 - "PostController"
Cohesion: 0.15
Nodes (3): CommentRequest, CreatePostRequest, PostController

### Community 19 - "JpaSocialRepository"
Cohesion: 0.17
Nodes (3): FollowEntity, JpaSocialRepository, SpringDataFollowRepository

### Community 21 - ".add"
Cohesion: 0.19
Nodes (3): AddBookRequest, LibraryController, UpdateBookRequest

### Community 23 - "InMemoryUserBookRepository"
Cohesion: 0.15
Nodes (3): FixedCatalogRepository, InMemoryUserBookRepository, LibraryServiceTest

### Community 24 - "SecurityConfiguration"
Cohesion: 0.24
Nodes (6): CorsConfigurationSource, HttpSecurity, JwtDecoder, SecretKey, SecurityFilterChain, SecurityConfiguration

### Community 25 - "JpaRepository"
Cohesion: 0.22
Nodes (5): JpaRepository, SpringDataBookWorkRepository, LikeEntity, SpringDataCommentRepository, SpringDataLikeRepository

### Community 26 - "SpringDataPostRepository"
Cohesion: 0.22
Nodes (3): Query, SpringDataPostRepository, PostEntity

### Community 27 - ".finish"
Cohesion: 0.18
Nodes (3): UserBook, ReadingGoal, StatisticsController

### Community 28 - "ProfileMetricsService"
Cohesion: 0.31
Nodes (5): ProfileMetricsService, PublicUserResponse, UpdateProfileRequest, UserController, UserResponse

### Community 29 - "InMemoryUserRepository"
Cohesion: 0.17
Nodes (3): RegisterCommand, InMemoryUserRepository, UserRegistrationServiceTest

### Community 30 - ".calculate"
Cohesion: 0.19
Nodes (4): ConsistencySummary, DailyReading, ReadingConsistencyCalculator, ReadingConsistencyCalculatorTest

### Community 31 - "InMemoryNoteRepository"
Cohesion: 0.22
Nodes (3): CapturingPublisher, InMemoryNoteRepository, ReviewAndNoteServiceTest

### Community 32 - "UserRepository"
Cohesion: 0.26
Nodes (3): UserApplicationConfiguration, UserRegistrationService, UserRepository

### Community 33 - "JpaPasswordResetTokenRepositoryAdapter"
Cohesion: 0.24
Nodes (3): JpaPasswordResetTokenRepositoryAdapter, PasswordResetTokenEntity, SpringDataPasswordResetTokenRepository

### Community 35 - "JpaReadingGoalRepository"
Cohesion: 0.20
Nodes (3): JpaReadingGoalRepository, ReadingGoalEntity, SpringDataReadingGoalRepository

### Community 37 - "BookEditionEntity"
Cohesion: 0.31
Nodes (4): Pageable, Query, SpringDataBookEditionRepository, BookEditionEntity

### Community 38 - "Yomora API Application Configuration"
Cohesion: 0.24
Nodes (10): Caffeine Cache, CORS Allowed Origins Policy, Flyway Migrations, JPA Schema Validation, JWT Security Configuration, Health and Info Management Endpoints, PostgreSQL Datasource, Sanitized Server Error Responses (+2 more)

### Community 39 - "SocialPublisher"
Cohesion: 0.27
Nodes (3): SocialPublisher, ReviewConfiguration, SocialPublisherAdapter

### Community 42 - "BookProvider"
Cohesion: 0.32
Nodes (4): EnableCaching, FunctionalInterface, BookProvider, CatalogConfiguration

### Community 43 - "LocalPasswordResetMailbox"
Cohesion: 0.39
Nodes (3): LocalPasswordResetMailbox, LocalPasswordResetMailboxController, ResetTokenResponse

### Community 44 - "ReviewController"
Cohesion: 0.32
Nodes (3): ReviewController, ReviewRequest, UpdateReviewRequest

### Community 47 - "ReadingGoalController"
Cohesion: 0.38
Nodes (3): PutMapping, GoalRequest, ReadingGoalController

### Community 49 - "ReadingStatus"
Cohesion: 0.29
Nodes (6): ReadingStatus, ABANDONED, FINISHED, PAUSED, READING, WANT_TO_READ

### Community 51 - "PostType"
Cohesion: 0.29
Nodes (6): PostType, NOTE, PROGRESS, QUOTE, RECOMMENDATION, REVIEW

### Community 52 - "PasswordResetServiceTest.java"
Cohesion: 0.38
Nodes (3): CapturingNotifier, InMemoryPasswordResetTokenRepository, PasswordResetServiceTest

### Community 54 - "TestcontainersConfiguration.java"
Cohesion: 0.53
Nodes (4): PostgreSQLContainer, ServiceConnection, TestConfiguration, TestcontainersConfiguration

### Community 55 - "BookWorkEntity.java"
Cohesion: 0.47
Nodes (3): BookWorkEntity, Entity, Table

### Community 57 - "AuthApiIntegrationTest"
Cohesion: 0.60
Nodes (3): AutoConfigureMockMvc, MockMvc, AuthApiIntegrationTest

### Community 58 - "YomoraApiApplication"
Cohesion: 0.60
Nodes (3): ConfigurationPropertiesScan, SpringBootApplication, YomoraApiApplication

### Community 59 - "Q: Existe uma API de livros mais completa para buscas como Paul Washer?"
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Existe uma API de livros mais completa para buscas como Paul Washer?, Source Nodes

### Community 60 - "Q: Vamos seguir nisso então, teste bem porque antes a gente teve que trocar pt por por, porque era o formato aceito. Então veja o que funciona."
Cohesion: 0.40
Nodes (4): Answer, Outcome, Q: Vamos seguir nisso então, teste bem porque antes a gente teve que trocar pt por por, porque era o formato aceito. Então veja o que funciona., Source Nodes

### Community 61 - "Visibility"
Cohesion: 0.40
Nodes (4): Visibility, FOLLOWERS, PRIVATE, PUBLIC

### Community 62 - "ModularArchitectureTest.java"
Cohesion: 0.83
Nodes (3): AnalyzeClasses, ArchRule, ModularArchitectureTest

### Community 63 - "SecurityProperties.java"
Cohesion: 0.83
Nodes (3): ConfigurationProperties, SecurityProperties, Validated

### Community 64 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **33 isolated node(s):** `ProfileMetrics`, `TokenPair`, `WANT_TO_READ`, `READING`, `PAUSED` (+28 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **268 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `SocialRepository` connect `SocialRepository` to `SocialService`, `PersistenceQueryIntegrationTest`, `FollowController`, `PostController`, `JpaSocialRepository`, `InMemorySocialRepository`, `.combinesSocialAndReadingMetricsForAProfile`, `ProfileMetricsService`?**
  _High betweenness centrality (0.088) - this node is a cross-community bridge._
- **Why does `UserRepository` connect `UserRepository` to `AuthenticationService`, `PasswordResetTokenRepository`, `.update`, `JpaUserRepositoryAdapter`, `InMemoryUserRepository`, `PasswordResetServiceTest.java`, `InMemoryUserRepository`, `ProfileMetricsService`, `InMemoryUserRepository`?**
  _High betweenness centrality (0.088) - this node is a cross-community bridge._
- **Why does `BookCandidate` connect `BookCandidate` to `PersistenceQueryIntegrationTest`, `BookProviderQuery`, `BookCandidate.java`?**
  _High betweenness centrality (0.068) - this node is a cross-community bridge._
- **What connects `ProfileMetrics`, `TokenPair`, `WANT_TO_READ` to the rest of the system?**
  _33 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BookCandidate` be split into smaller, more focused modules?**
  _Cohesion score 0.06583850931677018 - nodes in this community are weakly interconnected._
- **Should `AuthenticationService` be split into smaller, more focused modules?**
  _Cohesion score 0.05925925925925926 - nodes in this community are weakly interconnected._
- **Should `ShelfRepository` be split into smaller, more focused modules?**
  _Cohesion score 0.06289308176100629 - nodes in this community are weakly interconnected._
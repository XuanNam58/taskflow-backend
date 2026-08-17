# Full-Stack Developer Learning Roadmap

**Owner:** Xuân Nam Đặng  
**Target:** Middle Full-Stack Developer  
**Primary stack:** Java, Spring Boot, PostgreSQL, Angular  
**Roadmap version:** 1.2  
**Created:** 2026-07-18  
**Last reviewed:** 2026-08-09  
**Review cadence:** After each milestone or every 4 weeks

---

## 1. Current Position

### Existing strengths

- Around one year of professional backend experience.
- Java and Spring Boot.
- PostgreSQL, SQL, JPA/Hibernate, and QueryDSL.
- REST API development.
- Redis and messaging exposure.
- Spring Batch and scheduled jobs.
- Flyway/Liquibase exposure.
- Git, Maven, Docker, and basic CI/CD awareness.
- Experience discussing performance, transactions, concurrency, authorization, and domain modeling.

### Current gaps

- Angular and production frontend architecture.
- Automated testing discipline.
- Consistent application of design patterns.
- End-to-end feature ownership.
- CI/CD implementation from scratch.
- Deployment, observability, and cloud fundamentals.
- Broader system-design reasoning.
- Demonstrating middle-level ownership through a complete project.
- Depth in applied security — the authentication code compiles and looks correct but carries several
  real defects (see the progress board). Added 2026-08-01.
- Working through pull requests. The repository currently has no branches and no pull requests, which
  the Phase 10 portfolio list requires. Added 2026-08-01.

---

## 2. What “Middle Full-Stack Developer” Means

The target is not knowing every technology. The target is being able to independently deliver a feature from requirement to production.

A middle full-stack developer should be able to:

1. Clarify requirements and identify edge cases.
2. Design database changes and API contracts.
3. Implement maintainable backend code.
4. Write unit and integration tests for important behavior.
5. Build the frontend workflow and manage application state.
6. Handle authentication, authorization, validation, and errors.
7. Review code and explain technical trade-offs.
8. Use Git effectively and work through pull requests.
9. Build and deploy the system through CI/CD.
10. Investigate logs, performance problems, and production failures.
11. Make reasonable architectural decisions without overengineering.
12. Complete features independently while knowing when to ask for help.

---

## 3. Core Learning Strategy

### Main rule: build vertical slices

Do not finish the entire backend before starting the frontend.

Use this cycle:

```text
Requirement
→ domain and API design
→ backend implementation
→ unit/integration tests
→ Angular implementation
→ manual and end-to-end verification
→ refactor
→ pull-request review
→ deploy
```

Example:

```text
Authentication backend
→ authentication tests
→ authentication frontend

Workspace backend
→ workspace tests
→ workspace frontend

Project backend
→ project tests
→ project frontend
```

### Main project

**TaskFlow** will be the primary learning project.

Repositories:

- `taskflow-backend`
- `taskflow-frontend`

It should be developed as a production-style application rather than a tutorial project.

### Learning ratio

- 70% building and debugging.
- 20% reading documentation and studying concepts.
- 10% notes, summaries, and interview preparation.

---

# 4. Roadmap Phases

## Phase 1 — Production Backend Foundation

**Goal:** Build a stable foundation and complete authentication correctly.

### Technologies

- Java 21 or another current LTS version supported by the project.
- Spring Boot 3.x.
- Spring Security.
- Spring Data JPA.
- QueryDSL when dynamic or complex queries justify it.
- PostgreSQL.
- Flyway.
- MapStruct.
- Bean Validation.
- Maven.
- Docker Compose.
- Testcontainers.
- JUnit 5.
- Mockito.
- MockMvc.

### TaskFlow scope

- Registration.
- Login.
- Access-token authentication.
- Refresh-token rotation.
- Logout current device.
- Logout all devices.
- Current-user endpoint.
- Standard API error responses.
- Validation.
- Security whitelist.
- Disabled-user handling.
- Password change.
- Forgot/reset password.
- Optional email verification.

### Testing milestone

Before moving deeply into Angular, implement authentication integration tests with PostgreSQL Testcontainers.

Important cases:

- Successful registration.
- Duplicate email and username.
- Validation errors.
- Successful login.
- Invalid credentials.
- Disabled user.
- Successful token rotation.
- Expired refresh token.
- Revoked refresh token.
- Reuse of old token.
- Concurrent refresh requests.
- Logout.
- Logout all devices.
- Protected endpoint without access token.
- Invalid and expired access token.

### Design patterns introduced

- Dependency Injection.
- Repository pattern.
- Service layer.
- DTO pattern.
- Mapper pattern.
- Strategy through Spring Security components.
- Adapter around external services later.
- Factory method only where object creation becomes complex.

### Exit criteria

- Authentication integration tests pass.
- Flyway can create a fresh database.
- Error responses are consistent.
- Security behavior is documented.
- Backend can run through Docker Compose.
- Code is reviewed and refactored.

---

## Phase 2 — Angular Foundation and Authentication

**Goal:** Become comfortable creating a production-style Angular feature.

### Technologies

- TypeScript.
- Angular.
- Angular Router.
- Reactive Forms.
- HttpClient.
- Functional interceptors or the current recommended Angular equivalent.
- Route guards.
- Signals for local application state.
- RxJS for asynchronous streams where it adds value.
- SCSS or a consistent styling solution.
- Angular testing tools.
- Playwright later for end-to-end tests.

### Concepts

- Components and templates.
- Data binding.
- Inputs and outputs.
- Services and dependency injection.
- Reactive forms and validation.
- Routing and nested routes.
- HTTP requests.
- Interceptors.
- Authentication state.
- Token refresh flow.
- Error handling.
- Loading and empty states.
- Reusable components.
- Container/presentational separation where useful.

### TaskFlow scope

- Login page.
- Registration page.
- Authentication service.
- Access-token handling.
- Refresh-token workflow.
- Protected routes.
- Current-user state.
- Logout.
- Logout all devices.
- Basic layout.
- Error messages and form validation.

### Important architectural decision

Do not scatter token logic across components.

Use clear responsibilities:

```text
AuthApiService
→ HTTP calls

AuthStore/AuthStateService
→ current authentication state

AuthInterceptor
→ Authorization header and refresh handling

AuthGuard
→ route protection

LoginComponent/RegisterComponent
→ UI and form behavior
```

### Exit criteria

- User can register, log in, reload the page, refresh a token, access a protected page, and log out.
- Authentication logic is not duplicated across components.
- Frontend errors map cleanly to backend error codes.
- Basic component and service tests exist.
- The complete authentication slice works locally.

---

## Phase 3 — Testing Discipline

**Goal:** Learn which test type belongs to which problem.

Testing begins during Phase 1 and continues in every later phase. This phase represents reaching consistent discipline, not postponing tests.

### Unit tests

Use for isolated business logic:

- Date calculations.
- Permission decisions.
- State transitions.
- Ranking/order algorithms.
- Pricing or quota calculation.
- Mappers with custom logic.
- Utilities.
- Domain policies.

Avoid tests that merely mock a repository and repeat implementation details.

### Integration tests

Use for:

- Controllers and validation.
- Spring Security.
- JPA mappings.
- Flyway migrations.
- PostgreSQL constraints.
- QueryDSL queries.
- Transactions.
- Locking and concurrency.
- Repository behavior.
- API error responses.

Preferred tools:

- `@SpringBootTest` or focused test slices where appropriate.
- MockMvc.
- Testcontainers PostgreSQL.
- Awaitility for asynchronous behavior when needed.

### Frontend tests

Use for:

- Form validation.
- Services and interceptors.
- State-management behavior.
- Important reusable components.
- Route guards.

### End-to-end tests

Use only for critical journeys:

- Register and log in.
- Create workspace and project.
- Create and update a task.
- Assign a task.
- Complete a task.
- Log out.

Use Playwright after the first core TaskFlow workflow exists.

### Coverage philosophy

Do not target 100% coverage.

Prioritize:

- Security.
- Permissions.
- Transactions.
- State transitions.
- Concurrency.
- Database constraints.
- Complex queries.
- Important user journeys.

---

## Phase 4 — Workspace, Project, and Task Domain

**Goal:** Build the core application and learn domain modeling.

### Workspace module

Features:

- Create workspace.
- List user workspaces.
- Update workspace.
- Archive or soft-delete workspace.
- Workspace members.
- Roles and permissions.

Concepts:

- Ownership.
- Membership.
- Tenant boundaries.
- Soft deletion.
- Authorization at service level.
- Unique constraints.
- Pagination.

### Project module

Features:

- Create and update projects.
- Project status.
- Project membership.
- Archive project.
- Filter, search, sort, and paginate.
- Project dashboard.

Concepts:

- Aggregate boundaries.
- Status transitions.
- Dynamic QueryDSL filtering.
- Optimistic locking.
- Audit fields.
- API contract design.

### Task module

Features:

- Create task.
- Update task.
- Assign task.
- Priority and status.
- Due dates.
- Labels.
- Comments.
- Attachments.
- Activity history.
- Task filtering and search.
- Ordering within boards or lists.

Concepts:

- Richer domain rules.
- Transaction boundaries.
- Event-driven side effects.
- File storage.
- N+1 prevention.
- Projections.
- Batch fetching.
- Permission checks.
- State-machine thinking.

### Full-stack workflow

For each module:

1. Design schema and API.
2. Implement migrations.
3. Implement backend.
4. Add integration tests.
5. Implement Angular screens.
6. Add essential frontend tests.
7. Review performance and security.
8. Merge through a pull request.

### Exit criteria

A user can:

- Create a workspace.
- Invite or manage members.
- Create a project.
- Create, assign, update, and complete tasks.
- Search and filter tasks.
- See a usable project dashboard.

---

## Phase 5 — Design Patterns Through Real Problems

**Goal:** Understand patterns from their purpose, not memorize definitions.

### Patterns to study first

#### 1. Strategy

Use for interchangeable policies:

- Task sorting.
- Notification channel.
- File-storage provider.
- Permission policy.
- Pricing or quota policy.

#### 2. Factory Method

Use when object creation depends on type:

- Notification sender creation.
- Exporter selection.
- Task activity creation.
- File-storage provider selection.

#### 3. Template Method

Use carefully for workflows sharing stable steps:

- Import processing.
- Report generation.
- File conversion.

Prefer composition when subclasses would become tightly coupled.

#### 4. Observer / Domain Events

Use for side effects:

- Task assigned → notification.
- Password changed → revoke sessions.
- Project archived → activity record.
- Comment created → mention notifications.

Start with Spring application events. Move to Kafka/RabbitMQ only when asynchronous or cross-service communication is justified.

#### 5. Adapter

Use around infrastructure:

- Email provider.
- Cloud storage.
- Third-party identity provider.
- Payment or messaging system.

The domain should depend on an interface, not the provider SDK.

#### 6. Decorator

Use for cross-cutting behavior when appropriate:

- Caching.
- Metrics.
- Retry.
- Auditing.

Often Spring AOP or framework features already provide this.

#### 7. Builder

Use for complex immutable objects and test fixtures.

Do not use it automatically for simple objects.

#### 8. Specification

Use for composable query/filter conditions.

QueryDSL predicates can play this role.

#### 9. State

Study after TaskFlow has meaningful task/project transitions.

Use when behavior changes substantially based on state. Do not force it onto simple enums.

#### 10. Chain of Responsibility

Useful for:

- Validation pipelines.
- Request processing.
- Import rules.
- Security filters.

Spring Security already demonstrates this pattern.

### Pattern-learning format

For each pattern:

1. Problem without the pattern.
2. Why the current code becomes difficult.
3. Pattern structure.
4. Apply it to TaskFlow.
5. Identify costs and cases where it should not be used.
6. Write or refactor code.
7. Review the result.

### Anti-patterns to recognize

- God service.
- Anemic service/controller duplication.
- Circular dependencies.
- Excessive inheritance.
- Generic utility classes for domain logic.
- Returning JPA entities directly.
- Catching `Exception` everywhere.
- Premature abstraction.
- Using patterns only to make code look advanced.
- Microservices before the domain and operational need justify them.

---

## Phase 6 — Performance and Data Access

**Goal:** Become capable of diagnosing normal backend performance problems.

### Topics

- SQL execution plans.
- Index design.
- Composite indexes.
- Selectivity.
- Pagination.
- Offset versus cursor pagination.
- N+1 queries.
- Fetch joins.
- Entity graphs.
- DTO projections.
- Batch fetching.
- Connection pools.
- Transaction duration.
- Optimistic and pessimistic locking.
- Deadlocks.
- Idempotency.
- Redis caching.
- Cache invalidation.
- Rate limiting.

### TaskFlow exercises

- Measure project/task listing queries.
- Add indexes based on actual query patterns.
- Compare entity loading with DTO projection.
- Introduce Redis for one justified use case.
- Implement cache eviction correctly.
- Add an idempotency key to an appropriate write endpoint.
- Reproduce and handle a concurrency issue.

### Exit criteria

- Can use `EXPLAIN ANALYZE`.
- Can identify an N+1 problem.
- Can explain why an index helps or does not help.
- Can choose optimistic versus pessimistic locking.
- Can explain cache consistency trade-offs.
- Can demonstrate performance improvement with measurements.

---

## Phase 7 — Background Jobs, Messaging, and Notifications

**Goal:** Build asynchronous workflows responsibly.

### Technologies

- Spring scheduling for simple single-instance jobs.
- Spring Batch for restartable, chunk-based, or large-data jobs.
- Redis for caching or lightweight coordination where appropriate.
- RabbitMQ or Kafka when messaging requirements appear.
- WebSocket or Server-Sent Events for realtime updates.
- Email provider abstraction.

### TaskFlow features

- Due-date reminder job.
- Daily summary email.
- Notification center.
- Realtime task assignment notification.
- Activity export.
- Batch cleanup of expired tokens or temporary files.

### Decision rules

Use a scheduler when:

- The task is time-based.
- It is acceptable to run within the same service.
- The work is small and simple.

Use Spring Batch when:

- Processing many records.
- Restartability matters.
- Chunking and retry are needed.
- Job history matters.

Use messaging when:

- Producers and consumers should be decoupled.
- Work should be asynchronous.
- Retry and delivery semantics matter.
- Multiple services or consumers are involved.

### Exit criteria

- Can explain at-least-once delivery.
- Implements idempotent consumers.
- Handles retry and dead-letter behavior.
- Understands why uncaught async exceptions are dangerous.
- Can choose between scheduler, batch, and messaging.

---

## Phase 8 — Docker, CI/CD, Deployment, and Observability

**Goal:** Own the path from commit to running application.

### Docker

- Backend Dockerfile.
- Frontend Dockerfile.
- Multi-stage builds.
- Docker Compose for local development.
- PostgreSQL and Redis containers.
- Environment-based configuration.
- Health checks.

### CI/CD

Start with GitHub Actions or GitLab CI based on repository hosting.

Pipeline stages:

```text
Backend compile
→ unit tests
→ integration tests with PostgreSQL
→ frontend lint
→ frontend tests
→ frontend build
→ container build
→ security/dependency checks
→ deploy to staging
```

### Deployment progression

1. Local Docker Compose.
2. Free or low-cost platform for staging.
3. Linux VM deployment.
4. AWS fundamentals.
5. Optional managed services.

### AWS learning order

Do not begin by buying an expensive broad course.

Study:

- IAM.
- EC2.
- VPC basics.
- Security groups.
- RDS.
- S3.
- CloudWatch.
- Route 53.
- Application Load Balancer.
- ECS or another container deployment option.
- Secrets Manager or Parameter Store.

Use the AWS Free Tier carefully and set billing alarms.

### Observability

- Structured logs.
- Correlation/request ID.
- Health endpoints.
- Metrics with Micrometer.
- Prometheus and Grafana locally.
- Error tracking.
- Slow-query logging.
- Basic alerting.

### Exit criteria

- Push to main triggers tests and builds.
- Staging deployment is repeatable.
- Secrets are not committed.
- Database migrations run safely.
- Application health and logs are visible.
- Rollback procedure is documented.

---

## Phase 9 — System Design Foundations

**Goal:** Develop middle-level architectural reasoning without jumping too early into massive distributed systems.

### Topics

- Requirements clarification.
- Functional and non-functional requirements.
- Capacity estimation.
- API design.
- Database modeling.
- Consistency and transactions.
- Caching.
- Queues.
- Horizontal scaling.
- Load balancing.
- File storage.
- Search.
- Observability.
- Failure handling.
- Security.
- Trade-off communication.

### TaskFlow design exercises

- Design attachment upload.
- Design notification delivery.
- Design task search.
- Design audit history.
- Design activity feed.
- Design multi-tenant permissions.
- Design reminder scheduling.
- Design high-volume task listing.
- Design realtime collaboration as a future extension.

### What not to prioritize yet

- Building microservices just to learn microservices.
- Kubernetes before Docker and CI/CD are comfortable.
- Complex distributed consistency algorithms without a project need.
- Memorizing architecture diagrams without implementing the underlying ideas.

---

## Phase 10 — Portfolio and Interview Readiness

**Goal:** Make skills visible and explainable.

### TaskFlow portfolio requirements

- Clear README.
- Architecture diagram.
- ER diagram.
- Local setup instructions.
- API documentation.
- Testing strategy.
- CI/CD pipeline.
- Screenshots or demo.
- Deployment link.
- Technical decisions and trade-offs.
- Known limitations.
- Roadmap.
- Meaningful Git history.
- Pull requests showing review and refactoring.

### Interview preparation

- Java fundamentals.
- Collections.
- Concurrency.
- JVM basics.
- Spring dependency injection and proxies.
- Transactions.
- JPA lifecycle.
- SQL and indexing.
- REST semantics.
- Authentication and authorization.
- Testing.
- Git.
- Docker.
- CI/CD.
- Angular fundamentals.
- TypeScript.
- RxJS and Signals.
- System-design basics.
- Behavioral stories from TaskFlow and professional work.

Use TaskFlow examples rather than giving only textbook answers.

---

# 5. Secondary Projects

TaskFlow remains the main project. Add secondary projects only when they teach a clearly different skill.

## Project 2 — Realtime Collaboration Mini-Project

Possible scope:

- Shared board or document presence.
- WebSocket communication.
- Optimistic UI.
- Reconnection.
- Event ordering.
- Basic conflict handling.

Purpose:

- Realtime frontend/backend interaction.
- WebSocket lifecycle.
- Concurrency thinking.

## Project 3 — Batch and Data Processing Mini-Project

Possible scope:

- CSV import.
- Validation report.
- Chunk processing.
- Retry and skip.
- Scheduled processing.
- Export.

Purpose:

- Spring Batch.
- Large-data handling.
- Job monitoring.

## Project 4 — FX Rate Aggregator (WebFlux + ELK vehicle)

Added 2026-08-01 in response to job postings asking about the ELK stack and WebFlux. These two
technologies are deliberately **not** added to TaskFlow — see the reasoning below.

Possible scope:

- WebFlux + WebClient fanning out concurrently to three mock rate providers, returning the best quote.
- SSE endpoint streaming live rate ticks.
- Resilience4j timeout, retry, and circuit breaker per provider.
- Deliberately no JPA. Use R2DBC only if persistence is actually needed.
- Both this service and TaskFlow emit structured JSON logs carrying a shared `X-Request-Id`, shipped to
  ELK (or Loki + Grafana as a lighter alternative) so one user action can be traced across two services.

Purpose:

- WebFlux applied where reactive genuinely wins: I/O-bound fan-out and streaming, not CRUD.
- Understanding why blocking JPA must not appear in a reactive stack.
- ELK with a real justification — correlating a request across more than one service.

### Why not inside TaskFlow

TaskFlow is transactional CRUD over JPA. WebFlux there would still block on JPA calls, giving worse
throughput than Spring MVC and no defensible answer to "why did you choose WebFlux?". ELK against a
single service is decoration rather than observability.

### Cheap step to take inside TaskFlow now

Add a Logback JSON encoder plus a filter that puts a correlation ID into MDC (roughly thirty lines).
This delivers most of the interview value of the ELK topic at almost no cost, fixes the current problem
that nothing in the codebase logs anything, and makes wiring the real ELK stack later trivial.

Do not start these until TaskFlow has a complete deployable core.

---

# 6. Suggested Timeline

This is progress-based rather than deadline-based. Exact duration depends on work schedule.

## Stage A — Foundation

- Finish authentication.
- Add backend integration tests.
- Build Angular authentication.
- Dockerize local development.

## Stage B — Core product

- Workspace.
- Projects.
- Tasks.
- Permissions.
- Comments.
- Attachments.
- Search/filtering.

## Stage C — Production quality

- CI/CD.
- Staging deployment.
- Logging and metrics.
- Performance review.
- Security review.
- End-to-end tests.

## Stage D — Advanced features

- Notifications.
- Realtime updates.
- Scheduled reminders.
- Redis.
- Messaging.
- Batch exports/imports.

## Stage E — Middle-level readiness

- Architecture documentation.
- Design-pattern review.
- System-design exercises.
- Interview preparation.
- Portfolio polish.
- Independent feature-delivery assessment.

---

# 7. Weekly Learning Loop

A practical weekly cycle:

## Build

Implement one small vertical slice.

## Test

Add tests for its highest-risk behavior.

## Review

Review:

- Responsibility boundaries.
- Naming.
- Transactions.
- Query count.
- Security.
- Error behavior.
- API usability.
- Frontend state.
- Missing tests.

## Refactor

Make one meaningful improvement rather than many cosmetic changes.

## Document

Record:

- What was built.
- Important decisions.
- Problems encountered.
- What was learned.
- Next step.

## Explain

Practice explaining one topic in English:

- Why this transaction boundary?
- Why this relationship?
- Why integration test instead of unit test?
- Why use or avoid Redis?
- Why this Angular state design?

---

# 8. Review Checklist for Every Feature

## Requirement

- What user problem does this solve?
- What are the success and failure cases?
- What permissions are required?
- What happens under duplicate or concurrent requests?

## Backend

- Is the controller thin?
- Does the service own business behavior?
- Is the transaction boundary correct?
- Are entities hidden behind DTOs?
- Are validation and errors consistent?
- Are database constraints present?
- Are queries efficient?

## Security

- Is the endpoint public or protected intentionally?
- Is authorization checked at the correct level?
- Can one user access another user's data?
- Are secrets and sensitive data protected?
- Is logging safe?

## Testing

- Which behavior needs unit tests?
- Which behavior requires integration tests?
- Is PostgreSQL-specific behavior tested with PostgreSQL?
- Are concurrency and transaction cases covered?
- Is there one useful end-to-end test?

## Frontend

- Is API logic outside the component?
- Is state ownership clear?
- Are loading, empty, success, and error states handled?
- Are forms validated?
- Are reusable components actually reusable?
- Is the UI protected from duplicate requests?

## Operations

- Is configuration externalized?
- Are migrations safe?
- Are logs sufficient for debugging?
- Does the health check reflect dependencies?
- Can the feature be deployed and rolled back?

---

# 9. Progress Board

**Rule added 2026-08-01:** a feature counts as done only when a test proves it. Written is not done.
The previous version of this board tracked features written, which is why the defects below went
unnoticed.

## Done and proven by a test

As of 2026-08-09: 10 tests across 3 integration-test classes, suite green.

- Backend project setup.
- PostgreSQL and Flyway foundation.
- Testcontainers integration-test foundation — singleton container shared across all test classes.
- User registration, including the duplicate-email conflict.
- Login: rejects a disabled account, and returns both tokens for a valid one.
- JWT authentication filter: an expired access token returns `AUTH_003` at the correct path.
- Refresh-token rotation: issues a new pair and revokes the old token.
- Refresh by a disabled user is rejected **and rolls back**, leaving the old token usable.
- Logout cannot revoke another user's refresh token.
- Logout revokes the token, and reusing it afterwards fails.

## Defects fixed in this milestone

Four of the six defects found on 2026-08-01 are closed, each with a test that failed first.

| Defect | Fix |
|---|---|
| `JwtException` escaped the authentication filter, so expired tokens returned `AUTH_002` with `path=/error` and the `ACCESS_TOKEN_EXPIRED` branch was unreachable | caught in the filter's catch clause (PR #4) |
| Login accepted a disabled account | `isEnabled()` checked **after** password verification, so a disabled account is not revealed to someone without the password (PR #6) |
| `refresh` was not transactional, so a failure after rotation left the old token revoked and the new one undelivered | `@Transactional` on `AuthServiceImpl.refresh` |
| `revoke` never verified token ownership | `logout` now takes the caller's id from `@AuthenticationPrincipal` and returns `AUTH_004` rather than confirming the token exists |

## Backlog — carried, not forgotten

**Gated: must be done before the first public deployment.**

- Hash refresh tokens with SHA-256. Stored in plaintext today, so a database dump is every live session.
  SHA-256 rather than BCrypt: the token is already 512 bits of entropy, and BCrypt's per-row salt would
  make lookup by hash impossible. Needs a Flyway migration; existing rows cannot be converted, so they
  are truncated and everyone logs in again.
- Refresh-token reuse detection (RFC 9700). Open design question: revoke per-user (reuse the existing
  `revokeAllByUserId`, no migration) or per-token-family (`family_id` column, needs a migration). Note
  that concurrent refresh from two browser tabs is normal and would trip naive detection, so a short
  grace window is likely needed.
- `docker-compose.yml` — required by the Phase 1 exit criteria.

**Refactor, no deadline.** Confine jjwt to `JwtServiceImpl` using the Adapter pattern from Phase 5:
`parseClaims` translates jjwt exceptions into `BusinessException` carrying an `ErrorCode`, so the filter
and the entry point stop importing `io.jsonwebtoken` and the fragile
`getCause() instanceof ExpiredJwtException` check disappears. Chosen deliberately over the one-line fix
that shipped, to keep the bug fix and the refactor in separate commits. Revisit when touching auth again.

**Still not started.** Password change, forgot/reset password, email verification, CI pipeline,
OpenAPI documentation, structured logging with a correlation ID.

## Current milestone

**Angular authentication** — starting 2026-08-10.

The backend auth contract is stable and tested, so the frontend can be built against it:
`401 AUTH_003` means refresh, `401 AUTH_002` means log out, `403 AUTH_007` means the account is disabled.
None of the backlog items above change that contract.

| Target | Scope |
|---|---|
| 10–16 Aug | angular.dev Essentials and the interactive tutorial. No TaskFlow code — a framework needs a structured backbone, not ad-hoc questions. |
| 17–23 Aug | `taskflow-frontend` scaffold. Login page calling the real API, access token stored, one protected route reachable. |
| 24–31 Aug | `AuthInterceptor`, `AuthGuard`, automatic refresh, current-user state, logout. |

Priority note: joining an Angular project at work is the immediate goal, which is why the backend
backlog is gated rather than scheduled.

## Next milestone

**Workspace vertical slice** — backend, tests and Angular screens together, per the Phase 4 workflow.

---

# 10. Source-of-Truth Rules

This file is the canonical learning roadmap.

At the end of a meaningful milestone:

1. Update the progress board.
2. Record important architectural decisions.
3. Adjust upcoming priorities.
4. Increase the roadmap version.
5. Preserve completed sections instead of rewriting history.

Future review requests should reference this file and ask:

```text
Review my current progress against the Full-Stack Developer Learning Roadmap.
Update completed milestones, identify gaps, and recommend the next highest-value task.
```

---

# 11. Current Recommendation

Do not add more authentication features immediately.

The next work should be:

```text
Authentication integration tests
→ Angular authentication
→ Workspace backend and tests
→ Workspace frontend
→ Project vertical slice
→ Task vertical slice
```

Testing and frontend development should now happen continuously alongside backend development.

The main objective is no longer merely learning individual technologies. It is repeatedly completing production-style vertical features and explaining the technical decisions behind them.

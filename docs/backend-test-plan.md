# Backend Test Suite — Concerts Booking API

## Context

The backend (`backend/`, Spring Boot 4.0.6 / Java 25, Maven) currently has **zero tests** —
`src/test/java` is empty and there are no test dependencies beyond the defaults. The booking
domain has real business rules worth protecting against regressions: seat availability checks,
future-concert validation, booking ownership, idempotent cancellation, and `@Version`
optimistic locking on `Concert`.

**Goal:** add a focused test suite using the **"Services + integration"** strategy:
fast Mockito unit tests for service/domain logic, plus a handful of end-to-end API flows
backed by a **real PostgreSQL instance via Testcontainers** (matches production, exercises
JPA/Hibernate and optimistic locking faithfully). Slice tests (`@DataJpaTest`/`@WebMvcTest`)
are intentionally skipped to keep the suite lean.

## Prerequisites

- **Docker must be available** locally and in CI — Testcontainers starts a throwaway Postgres
  container. (Already chosen by the user with this trade-off in mind.)

## Step 1 — Add test dependencies (`backend/pom.xml`)

Spring Boot 4's parent already manages Testcontainers versions, so no explicit versions are
needed. Add to `<dependencies>` (all `test` scope):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-testcontainers</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

`spring-boot-starter-test` (JUnit 5, Mockito, AssertJ, MockMvc) and `spring-security-test`
are already present.

## Step 2 — Isolate the data seeder from tests

`config/DataInitializer.java` is a `CommandLineRunner` that seeds a `demo` user and 5 concerts
on startup. In `@SpringBootTest` this would pollute every integration test. Guard it so it does
**not** run under the test profile:

- Annotate the class with `@Profile("!test")`.
- Integration tests run with `@ActiveProfiles("test")` (Step 4).

This is the only main-source change; it has no effect on normal runtime.

## Step 3 — Unit tests (Mockito, no Spring context)

Plain JUnit 5 + Mockito. Mock the repositories / `PasswordEncoder`; use **real** domain objects
(`Concert`, `Booking`) so `bookSeats()/releaseSeats()/cancel()` are genuinely exercised.

Location: `backend/src/test/java/com/darksoul/concertsbooking/`

- **`service/BookingServiceTest.java`** — covers `BookingService`:
  - `createBooking`: happy path (seats decremented, booking saved); concert not found → 404;
    past concert → 400; quantity > available seats → 400; quantity zero → 400.
  - `cancelBooking`: happy path (status CANCELLED, seats released); not found → 404;
    booking owned by another user → 403; already-cancelled → idempotent (no double release).
- **`service/UserServiceTest.java`** — covers `UserService`:
  - `register`: encodes password + saves; username trimmed; duplicate (case-insensitive) → 409.
  - `getByUsername`: found; not found → 401.
- **`service/ConcertServiceTest.java`** — `findFutureConcerts` delegates to the repository's
  derived query (verify the call).
- **`domain/ConcertTest.java`** — `bookSeats` reduces `availableSeats`; over-capacity throws;
  `releaseSeats` restores; never goes negative.
- **`domain/BookingTest.java`** — constructor computes `totalPrice = price × quantity`;
  `cancel()` sets status + `cancelledAt`.

## Step 4 — Integration tests (Testcontainers + real Postgres + MockMvc)

- **`src/test/resources/application-test.properties`** — minimal; datasource is supplied by
  `@ServiceConnection`, no URL/credentials needed.
- **`AbstractIntegrationTest.java`** — shared base class:
  - `@SpringBootTest(webEnvironment = MOCK)`, `@AutoConfigureMockMvc`, `@ActiveProfiles("test")`,
    `@Testcontainers`.
  - `static @Container PostgreSQLContainer<?>` annotated `@ServiceConnection` (Spring Boot 4
    wires the datasource automatically). `static` so the container is reused across all test
    classes that extend this base.
- **`web/AuthFlowIntegrationTest.java`** — register → 201; session reused for `GET /me`;
  duplicate → 409; bad credentials → 401; validation errors → 400.
- **`web/BookingFlowIntegrationTest.java`** — full end-to-end: register → seed concert → book
  → list → cancel; unauthenticated → 401; over-capacity → 400.

**Auth in tests:** drive the real register/login endpoints and reuse the `MockHttpSession` from
the response — this validates the session wiring in `AuthController.authenticate()`.

## Step 5 — Verify

```bash
cd backend
./mvnw test
```

Unit tests run instantly; the first integration test pulls/starts the Postgres container.
To run only unit tests during development:
```bash
mvn test -Dtest="*ServiceTest,*ConcertTest,*BookingTest"
```

## Files touched

| File | Change |
|---|---|
| `backend/pom.xml` | add 3 Testcontainers test deps |
| `backend/src/main/java/.../config/DataInitializer.java` | add `@Profile("!test")` |
| `backend/src/test/resources/application-test.properties` | new — test profile |
| `backend/src/test/java/.../AbstractIntegrationTest.java` | new — Testcontainers base |
| `backend/src/test/java/.../service/BookingServiceTest.java` | new |
| `backend/src/test/java/.../service/UserServiceTest.java` | new |
| `backend/src/test/java/.../service/ConcertServiceTest.java` | new |
| `backend/src/test/java/.../domain/ConcertTest.java` | new |
| `backend/src/test/java/.../domain/BookingTest.java` | new |
| `backend/src/test/java/.../web/AuthFlowIntegrationTest.java` | new |
| `backend/src/test/java/.../web/BookingFlowIntegrationTest.java` | new |

# PersonalFinance
> REST API for personal financial management — built with Spring Boot 3, JPA, JWT security, and PostgreSQL

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Apache Maven](https://img.shields.io/badge/Maven-3.9-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-GPL_3.0-blue)](#license)
[![GitHub tag](https://img.shields.io/github/tag/Patrickbpds/personal-finance?include_prereleases=&sort=semver)](https://github.com/Patrickbpds/personal-finance/releases/)

## Overview

Manage your personal finances through a secure REST API. Register users, track receivables and payables (titles), organize expenses by cost centers, and view a cash-flow dashboard — all protected by JWT authentication.

## Features

- **Authentication** — register, login, and self-service profile management with stateless JWT
- **Titles** — CRUD for financial records (receivables & payables) with date-range filtering, pagination and payment status (paid, overdue, pending)
- **Cost Centers** — categorize expenses with descriptive labels and pagination
- **Dashboard** — cash-flow summary aggregated in SQL, with totals per type and per payment status
- **Error Contract** — a single JSON shape for every failure, with consistent status codes
- **Rate Limiting** — brute-force protection on login, keyed by client IP and e-mail
- **API Docs** — Swagger UI enabled only in the `dev` profile
- **Health Check** — Spring Actuator endpoint at `/actuator/health`
- **Database Migrations** — Flyway-managed schema evolution

## Tech Stack

| Layer       | Technology                                                              |
|-------------|-------------------------------------------------------------------------|
| Language    | Java 21                                                                 |
| Framework   | Spring Boot 3.5, Spring Security, Spring Data JPA                       |
| Build       | Maven (via Maven Wrapper)                                               |
| Database    | PostgreSQL 17                                                           |
| Migrations  | Flyway 11                                                               |
| Auth        | JWT (Auth0 java-jwt)                                                    |
| Validation  | Jakarta Bean Validation                                                 |
| Rate Limit  | Bucket4j                                                               |
| API Docs    | Swagger / OpenAPI (springdoc-openapi, `dev` profile only)               |
| Testing     | JUnit 5, Mockito, MockMvc, Testcontainers                               |
| Coverage    | JaCoCo (enforced during `verify`)                                       |
| Containers  | Docker & Docker Compose                                                 |

## Quick Start

### Prerequisites

- Java 21 (JDK)
- Docker & Docker Compose (required for integration tests and optional for the database)

### 1. Clone & configure

```bash
git clone https://github.com/Patrickbpds/personal-finance.git
cd personal-finance
cp .env.exemplo .env
```

Edit `.env` with your PostgreSQL credentials, the JDBC URL used by Docker Compose and a JWT secret
with at least 32 characters.

### 2. Start the database

```bash
docker compose up -d java_db
```

### 3. Run the application

```bash
set -a && source .env && set +a
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/${POSTGRES_DB}"
export SPRING_DATASOURCE_USERNAME="${POSTGRES_USER}"
export SPRING_DATASOURCE_PASSWORD="${POSTGRES_PASSWORD}"
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080`.

To enable SQL logging and Swagger UI locally, run with the `dev` profile:

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

Swagger UI is then served at `http://localhost:8080/swagger-ui/index.html`.

### Or run everything with Docker

```bash
docker compose up -d --build
```

The image builds the application from source, runs it as a non-root user and exposes a
Docker `HEALTHCHECK` against `/actuator/health`. Docker Compose starts it with the default
(production-like) configuration; add `SPRING_PROFILES_ACTIVE=dev` to the `app` service to get
SQL logging and Swagger.

## Profiles

| Setting                          | Default (production-like) | `dev` |
|----------------------------------|---------------------------|-------|
| SQL logging (`show-sql`)          | off                       | on    |
| Swagger UI / `/v3/api-docs`       | disabled                  | enabled |
| Actuator health details           | hidden                    | shown |
| Actuator endpoints exposed        | `health` only             | `health` only |

`SWAGGER_ENABLED=true` enables the API docs without activating the whole `dev` profile.

## Running the tests

Unit tests run with Surefire; integration tests run with Failsafe against a disposable
PostgreSQL 17 container started by [Testcontainers](https://testcontainers.com/), so a
running Docker daemon is required (no local database needed).

```bash
./mvnw verify
```

`verify` also runs JaCoCo and fails the build when coverage drops below the gates:

- at least **60%** of instructions per class in the service and security packages;
- at least **80%** of instructions for the whole project.

The HTML report is written to `target/site/jacoco/index.html`.

If your user cannot reach the Docker socket directly, run the build through the `docker` group:

```bash
sg docker -c './mvnw verify'
```

## Usage

```bash
# Register a new user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"John","email":"john@example.com","password":"secret123"}'

# Login and get a JWT
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"secret123"}'

# Read your profile
curl http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <your-jwt-token>"

# List your titles, first page of 20 ordered by expiration date
curl "http://localhost:8080/api/titles?page=0&size=20&sort=expirationDate,asc" \
  -H "Authorization: Bearer <your-jwt-token>"

# Check cash flow
curl "http://localhost:8080/api/dashboard/cash-flow?startDate=2026-01-01T00:00:00&endDate=2026-12-31T23:59:59" \
  -H "Authorization: Bearer <your-jwt-token>"
```

Listing responses are `Page` objects: the items are in `content`, alongside `number`, `size`,
`totalElements` and `totalPages`.

Failed requests always answer with the same body:

```json
{
  "timestamp": "2026-01-01T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Title not found with id: ...",
  "path": "/api/titles/..."
}
```

## Project Structure

```
src/main/java/com/patrick/personalfinance/
├── PersonalFinanceApplication.java     # Entry point
├── application/
│   ├── dto/                            # Request & response DTOs
│   ├── mapper/                         # Entity <-> DTO mappers
│   ├── port/                           # CurrentUserProvider (current-user abstraction)
│   └── service/                        # Use cases: titles, center costs, dashboard, accounts
├── domain/
│   ├── entity/                         # JPA entities (User, Title, CenterCost)
│   ├── enums/                          # TitleType, PaymentStatus
│   ├── exceptions/                     # Custom exception classes
│   └── repository/                     # Spring Data JPA repositories
├── infrastructure/
│   ├── config/                         # OpenAPI configuration
│   ├── dto/                            # Error response DTO
│   └── handler/                        # Global exception handler
├── presentation/controller/            # REST controllers (auth, users, titles, costs, dashboard)
└── security/
    ├── config/                         # Security, CORS, JWT and BCrypt configuration
    ├── dto/                            # Login and token DTOs
    ├── filter/                         # JWT authentication filter
    ├── handler/                        # Authentication entry point
    ├── principal/                      # AuthenticatedUser (UserDetails adapter)
    ├── ratelimit/                      # Login attempt rate limiter
    └── service/                        # Authentication, JWT and UserDetails services
```

## API Endpoints

| Method | Endpoint                          | Auth | Description                          |
|--------|-----------------------------------|------|--------------------------------------|
| POST   | `/auth/register`                  | No   | Register a new user                  |
| POST   | `/auth/login`                     | No   | Login, returns JWT                   |
| GET    | `/api/users/me`                   | Yes  | Get the authenticated profile        |
| PUT    | `/api/users/me`                   | Yes  | Update own profile (password optional, requires current password) |
| DELETE | `/api/users/me`                   | Yes  | Deactivate own account (soft delete) |
| GET    | `/api/titles`                     | Yes  | List titles (paginated, current user) |
| GET    | `/api/titles/{id}`                | Yes  | Get title by ID                      |
| POST   | `/api/titles`                     | Yes  | Create title                         |
| PUT    | `/api/titles/{id}`                | Yes  | Update title                         |
| DELETE | `/api/titles/{id}`                | Yes  | Delete title                         |
| GET    | `/api/titles/by-expiration-date`  | Yes  | Filter by expiration date range      |
| GET    | `/api/titles/by-reference-date`   | Yes  | Filter by reference date range       |
| GET    | `/api/center-costs`               | Yes  | List cost centers (paginated, current user) |
| GET    | `/api/center-costs/{id}`          | Yes  | Get cost center by ID                |
| POST   | `/api/center-costs`               | Yes  | Create cost center                   |
| PUT    | `/api/center-costs/{id}`          | Yes  | Update cost center                   |
| DELETE | `/api/center-costs/{id}`          | Yes  | Delete cost center                   |
| GET    | `/api/dashboard/cash-flow`        | Yes  | Cash-flow summary and payment status |
| GET    | `/actuator/health`                | No   | Health check                         |
| GET    | `/swagger-ui/index.html`          | No   | Swagger UI (`dev` profile only)      |
| GET    | `/v3/api-docs`                    | No   | OpenAPI document (`dev` profile only) |

Every user-scoped operation reads the owner from the JWT: there is no way to list, read or change
another user's data through the API.

## Environment Variables

| Variable                          | Required | Description                                              |
|-----------------------------------|----------|----------------------------------------------------------|
| `SPRING_DATASOURCE_URL`           | Yes      | JDBC URL (e.g. `jdbc:postgresql://localhost:5432/finance`)|
| `SPRING_DATASOURCE_USERNAME`      | Yes      | Database user                                            |
| `SPRING_DATASOURCE_PASSWORD`      | Yes      | Database password                                        |
| `JWT_SECRET`                      | Yes      | Signing secret, at least 32 characters                   |
| `JWT_EXPIRATION`                  | No       | Token lifetime (default `24h`)                           |
| `CORS_ALLOWED_ORIGINS`            | No       | Comma-separated allowed origins (empty closes CORS)      |
| `SPRING_PROFILES_ACTIVE`          | No       | `dev` enables SQL logging, Swagger and health details    |
| `SWAGGER_ENABLED`                 | No       | `true` enables the API docs outside `dev`                |
| `LOGIN_RATE_LIMIT_ENABLED`        | No       | Enables login rate limiting (default `true`)             |
| `LOGIN_RATE_LIMIT_MAX_ATTEMPTS`   | No       | Attempts per window (default `5`)                        |
| `LOGIN_RATE_LIMIT_WINDOW`         | No       | Window length (default `15m`)                            |
| `POSTGRES_USER`                   | Compose  | Database user created by the Compose service             |
| `POSTGRES_PASSWORD`               | Compose  | Database password created by the Compose service         |
| `POSTGRES_DB`                     | Compose  | Database created by the Compose service                  |
| `DB_URL`                          | Compose  | JDBC URL passed to the `app` container                   |

## License

Released under [GPL 3.0](https://www.gnu.org/licenses/gpl-3.0) by [Patrick Batista](https://github.com/Patrickbpds).

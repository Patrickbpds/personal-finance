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

- **Authentication** — register, login, and profile management with stateless JWT
- **Titles** — CRUD for financial records (receivables & payables) with date-range filtering
- **Cost Centers** — categorize expenses with descriptive labels
- **Dashboard** — cash-flow summary aggregating receivables and payables within a period
- **API Docs** — integrated Swagger UI at `/swagger-ui/`
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
| API Docs    | Swagger / OpenAPI (springdoc-openapi)                                   |
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

Edit `.env` with your PostgreSQL credentials and a JWT secret.

### 2. Start the database

```bash
docker compose up -d db
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`. Swagger UI at `http://localhost:8080/swagger-ui/`.

### Or run everything with Docker

```bash
docker compose up -d --build
```

## Running the tests

Integration tests use [Testcontainers](https://testcontainers.com/) and start a disposable PostgreSQL 17
container, so a running Docker daemon is required (no local database needed).

```bash
./mvnw verify
```

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

# Use the token to access protected endpoints
curl http://localhost:8080/api/titles \
  -H "Authorization: Bearer <your-jwt-token>"

# Check cash flow
curl "http://localhost:8080/api/dashboard/cash-flow?startDate=2026-01-01&endDate=2026-12-31" \
  -H "Authorization: Bearer <your-jwt-token>"
```

## Project Structure

```
src/main/java/com/patrick/personalfinance/
├── PersonalFinanceApplication.java     # Entry point
├── application/
│   ├── dto/                            # Request & response DTOs
│   └── mapper/                         # Entity <-> DTO mappers
├── domain/
│   ├── entity/                         # JPA entities (User, Title, CenterCost)
│   ├── enums/                          # TypeTitle (RECEIVABLE, PAYABLE)
│   ├── exceptions/                     # Custom exception classes
│   └── repository/                     # Spring Data JPA repositories
├── infrastructure/
│   ├── config/                         # Swagger config
│   ├── dto/                            # Error response DTO
│   └── handler/                        # Global exception handler
├── presentation/controller/            # REST controllers
├── security/
│   ├── config/                         # Security & BCrypt config
│   ├── controller/                     # Auth controller
│   ├── dto/                            # Auth request/response DTOs
│   ├── filter/                         # JWT authentication filter
│   └── service/                        # Auth, JWT, & UserDetails services
└── service/                            # Business logic services
```

## API Endpoints

| Method | Endpoint                          | Auth | Description                      |
|--------|-----------------------------------|------|----------------------------------|
| POST   | `/auth/register`                  | No   | Register a new user              |
| POST   | `/auth/login`                     | No   | Login, returns JWT               |
| PUT    | `/auth/update`                    | Yes  | Update own profile               |
| GET    | `/api/users`                      | Yes  | List all users                   |
| GET    | `/api/users/{id}`                 | Yes  | Get user by ID                   |
| POST   | `/api/users`                      | Yes  | Create user (admin)              |
| PUT    | `/api/users/{id}`                 | Yes  | Update user                      |
| DELETE | `/api/users/{id}`                 | Yes  | Soft-delete user                 |
| GET    | `/api/titles`                     | Yes  | List titles (current user)       |
| GET    | `/api/titles/{id}`                | Yes  | Get title by ID                  |
| POST   | `/api/titles`                     | Yes  | Create title                     |
| PUT    | `/api/titles/{id}`                | Yes  | Update title                     |
| DELETE | `/api/titles/{id}`                | Yes  | Delete title                     |
| GET    | `/api/titles/by-expiration-date`  | Yes  | Filter by expiration date range  |
| GET    | `/api/titles/by-reference-date`   | Yes  | Filter by reference date range   |
| GET    | `/api/center-costs`               | Yes  | List cost centers (current user) |
| GET    | `/api/center-costs/{id}`          | Yes  | Get cost center by ID            |
| POST   | `/api/center-costs`               | Yes  | Create cost center               |
| PUT    | `/api/center-costs/{id}`          | Yes  | Update cost center               |
| DELETE | `/api/center-costs/{id}`          | Yes  | Delete cost center               |
| GET    | `/api/dashboard/cash-flow`        | Yes  | Cash-flow summary                |
| GET    | `/actuator/health`                | No   | Health check                     |

## Environment Variables

| Variable                     | Description                          |
|------------------------------|--------------------------------------|
| `SPRING_DATASOURCE_URL`      | JDBC URL (e.g. `jdbc:postgresql://...`) |
| `SPRING_DATASOURCE_USERNAME` | Database user                        |
| `SPRING_DATASOURCE_PASSWORD` | Database password                    |
| `JWT_SECRET`                 | Secret key for signing JWT tokens    |

## License

Released under [GPL 3.0](https://www.gnu.org/licenses/gpl-3.0) by [Patrick Batista](https://github.com/Patrickbpds).

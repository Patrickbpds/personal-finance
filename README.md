# 💰 Personal Finance - Personal Financial Management System

![Java](https://img.shields.io/badge/Java-24-orange?style=flat&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen?style=flat&logo=spring-boot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=flat&logo=postgresql)
![Angular](https://img.shields.io/badge/Angular-20-red?style=flat&logo=angular)
![Docker](https://img.shields.io/badge/Docker-✓-blue?style=flat&logo=docker)
![JWT](https://img.shields.io/badge/JWT-Auth-green?style=flat&logo=jsonwebtokens)
![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-blue?style=flat&logo=typescript)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS--38B2AC?style=flat&logo=tailwind-css)

> **A modern fullstack application for complete personal finance control, inspired by professional financial management solutions.**

---

## 📋 Table of Contents

- [About the Project](#about-the-project)
- [Current Status](#current-status)
- [Technologies Used](#technologies-used)
- [Implemented Features](#implemented-features)
- [Database Structure](#database-structure)
- [Installation and Setup](#installation-and-setup)
- [API Endpoints](#api-endpoints)
- [Contributing](#contributing)
- [License](#license)

---

## 🎯 About the Project

**Personal Finance** is a modern fullstack application for comprehensive personal finance management that allows users to:

### 🎯 Main Features
- ✅ **Complete Income and Expense Control**
- ✅ **Smart Categorization by Cost Centers**
- ✅ **Interactive Dashboard with Real-time Metrics**
- ✅ **Secure JWT Authentication System**
- ✅ **Detailed Financial Reports**
- 🔄 **Financial Goals Management** (in development)
- 🔄 **Due Date Notifications** (planned)
- 🔄 **Predictive Spending Analysis** (planned)

---

## ⚡ Current Status

### ✅ **MVP Backend Completed**

#### 🚀 **Base System Implemented**
- [x] **Clean Architecture with Spring Boot 3.5.4**
- [x] **Complete JWT Authentication**
- [x] **User System with Soft Delete**
- [x] **Complete Financial Titles CRUD**
- [x] **Cost Center Management**
- [x] **Dashboard with Cash Flow**
- [x] **Complete Docker Containerization**
- [x] **PostgreSQL 17 Database**
- [x] **Versioned Flyway Migrations**
- [x] **Swagger/OpenAPI Documentation**
- [x] **Global Exception Handling**
- [x] **Robust Input Validations**

#### 📊 **APIs Implemented**
- [x] **Authentication & Authorization**
    - Login/Register with JWT
    - Refresh tokens
    - Route protection
- [x] **User Management**
    - Complete CRUD
    - Profile management
    - Soft delete
- [x] **Financial Titles**
    - Income and expenses
    - Date filtering
    - Cost center association
- [x] **Cost Centers**
    - Custom categorization
    - Cost hierarchy
- [x] **Dashboard**
    - Cash flow by period
    - Financial metrics
    - Automatic summaries

---

## 🛠 Technologies Used

### 🎯 Backend Stack
```java
// Core Framework
Spring Boot 3.5.4
Spring Security
Spring Data JPA
Spring Web

// Database & Migration
PostgreSQL 17
Flyway Migration
Hibernate ORM

// Security & Auth
JWT (Auth0 Java-JWT)
BCrypt Password Encoding

// Documentation & API
Swagger/OpenAPI 3
Spring Boot Actuator

// Development Tools
Lombok
Maven
Docker & Docker Compose
```

### 🎨 Frontend Stack (Planned)
```typescript
// Core Framework
Angular 20
TypeScript 5.0+

### 🐳 DevOps & Infrastructure
```yaml
# Containerization
Docker
Docker Compose

# Database
PostgreSQL 17
Redis (planned for caching)

# CI/CD (Planned)
GitHub Actions
```

---

## ✨ Implemented Features

### 🔐 **Authentication & Authorization System**
- [x] **Complete JWT Authentication**
- [x] **User registration and login**
- [x] **Route protection with Spring Security**
- [x] **Automatic authentication middleware**
- [x] **Token validation and automatic refresh**

### 👤 **Advanced User Management**
- [x] **Complete CRUD with robust validations**
- [x] **Profile management with photo upload**
- [x] **Soft delete for data preservation**
- [x] **Unique email validation**
- [x] **BCrypt encryption for passwords**
- [x] **Automatic audit fields**

### 💰 **Complete Financial System**
- [x] **Title management (income/expenses)**
- [x] **Categorization by cost centers**
- [x] **Advanced date filtering**
- [x] **Business rule validations**
- [x] **Flexible many-to-many associations**
- [x] **Automatic balance calculations**

### 📊 **Dashboard & Analytics**
- [x] **Cash flow by custom period**
- [x] **Real-time financial metrics**
- [x] **Automatic income vs expenses separation**
- [x] **Balance calculation and projections**
- [x] **Optimized APIs for reports**

### 🗄 **Robust Infrastructure**
- [x] **PostgreSQL 17 database with optimized indexes**
- [x] **Versioned migrations with Flyway**
- [x] **Complete Docker containerization**
- [x] **Automated health checks**
- [x] **Swagger/OpenAPI API documentation**
- [x] **Global error handling**

---

## 🗃 Database Structure

### 📊 Current Schema (Implemented)

#### 👤 **users table**
```sql
- id_user (UUID, PK)
- name (VARCHAR(80), NOT NULL)
- email (VARCHAR(80), UNIQUE, NOT NULL)
- password (VARCHAR(255), NOT NULL)
- photo (TEXT)
- registration_date (TIMESTAMP)
- inactivation_date (TIMESTAMP)
- updated_at (TIMESTAMP)
```

#### 💰 **titles table**
```sql
- idTitle (UUID, PK)
- description (VARCHAR, NOT NULL)
- idUser (UUID, FK)
- type (ENUM: RECEIVABLE/PAYABLE)
- value (DOUBLE, NOT NULL)
- registrationDate (TIMESTAMP)
- referenceDate (TIMESTAMP)
- expirationDate (TIMESTAMP)
- paymentDate (TIMESTAMP)
- observation (TEXT)
```

#### 🏷️ **center_costs table**
```sql
- idCenterCost (UUID, PK)
- description (VARCHAR, NOT NULL)
- observation (TEXT)
- idUser (UUID, FK)
```

#### 🔗 **title_center_costs junction table**
```sql
- idTitle (UUID, FK)
- idCenterCost (UUID, FK)
```

---

## 🚀 Installation and Setup

### 📋 Prerequisites
- **Docker & Docker Compose**
- **Java 24** (for local development)
- **Node.js** (for frontend development)
- **Git**

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/Patrickbpds/personal-finance.git
cd personal-finance
```

### 2️⃣ Environment Configuration
```bash
# Copy the example environment file
cp .env.example .env

# Configure variables in .env file
POSTGRES_USER=finance_user
POSTGRES_PASSWORD=your_secure_password
POSTGRES_DB=finance_db
DB_URL=jdbc:postgresql://finance_db:5432/finance_db
JWT_SECRET=your-super-secure-jwt-secret-key-change-in-production
```

### 3️⃣ Docker Deployment (Recommended)
```bash
# Start all services
docker-compose up -d
```

### 4️⃣ Access the Application
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Frontend** (when implemented): http://localhost:4200

### 5️⃣ Local Development

#### Backend Development
```bash
# Step 1: Navigate to the backend directory
cd backend

# Step 2: Build the project with Maven (skip tests)
mvn clean install -DskipTests

# Step 3: Start Docker containers
docker compose up --build

# Step 4: Open Swagger UI
# Open your browser and go to:
# http://localhost:8080/swagger-ui.html
```

#### Frontend Development (Coming Soon)
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
ng serve

# Open browser at http://localhost:4200
```

---

## 🛡 API Endpoints

### 🔐 Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/login` | User login | ❌ |
| POST | `/auth/register` | User registration | ❌ |
| PUT | `/auth/update` | Update user profile | ✅ |

#### 🔓 Login Example
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "token_type": "Bearer",
  "expires_in": 86400
}
```

### 👤 User Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/users` | List all users | ✅ |
| GET | `/api/users/{id}` | Get user by ID | ✅ |
| POST | `/api/users` | Create new user | ✅ |
| PUT | `/api/users/{id}` | Update user | ✅ |
| DELETE | `/api/users/{id}` | Deactivate user | ✅ |

### 💰 Financial Titles

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/titles` | List user's titles | ✅ |
| GET | `/api/titles/{id}` | Get title by ID | ✅ |
| POST | `/api/titles` | Create new title | ✅ |
| PUT | `/api/titles/{id}` | Update title | ✅ |
| DELETE | `/api/titles/{id}` | Delete title | ✅ |
| GET | `/api/titles/by-expiration-date` | Filter by expiration date | ✅ |
| GET | `/api/titles/by-reference-date` | Filter by reference date | ✅ |

#### 💸 Create Title Example
```bash
curl -X POST http://localhost:8080/api/titles \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Monthly Salary",
    "type": "RECEIVABLE",
    "value": 5000.00,
    "expirationDate": "2024-12-31T23:59:59",
    "centerCostIds": ["uuid-center-cost-1"],
    "observation": "December salary payment"
  }'
```

### 🏷️ Cost Centers

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/center-costs` | List cost centers | ✅ |
| GET | `/api/center-costs/{id}` | Get cost center by ID | ✅ |
| POST | `/api/center-costs` | Create cost center | ✅ |
| PUT | `/api/center-costs/{id}` | Update cost center | ✅ |
| DELETE | `/api/center-costs/{id}` | Delete cost center | ✅ |

### 📊 Dashboard

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/dashboard/cash-flow` | Get cash flow data | ✅ |

#### 📈 Cash Flow Example
```bash
curl -X GET "http://localhost:8080/api/dashboard/cash-flow?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "totalPayable": 15000.00,
  "totalReceivable": 25000.00,
  "balance": 10000.00,
  "payableTitles": [...],
  "receivableTitles": [...]
}
```

---

## 🤝 Contributing

We welcome contributions! Please follow these steps:

### 🔄 Development Workflow
1. **Fork** the project
2. Create a **feature branch** (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add some amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. Open a **Pull Request**

### 📋 Contribution Guidelines
- Follow existing code patterns and conventions
- Write tests for new features
- Update documentation when necessary
- Use semantic commit messages
- Ensure all tests pass before submitting PR

### 🏷️ Commit Message Convention
```
feat: add new dashboard widget
fix: resolve user authentication bug  
docs: update API documentation
style: improve code formatting
refactor: restructure user service
test: add unit tests for title service
```

---

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

### Third-party Licenses

This project includes software components with their own licenses. See the [NOTICE](NOTICE) file for details.

![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)

---

## 👥 Authors & Contributors

- **Patrick Batista** - *Lead Developer* - [@Patrickbpds](https://github.com/Patrickbpds)

### 🙏 Acknowledgments

- **Spring Community** - Framework and documentation
- **Angular Team** - Frontend framework
- **PostgreSQL Team** - Database system
- **Docker Community** - Containerization platform

---

## 📞 Contact & Support

- **Email**: patrickbpds@outlook.com
- **LinkedIn**: [Patrick Batista](https://www.linkedin.com/in/patrick-development/)
- **GitHub**: [@Patrickbpds](https://github.com/Patrickbpds)
- **Project Repository**: [Personal Finance](https://github.com/Patrickbpds/personal-finance)

---

<div align="center">

**⭐ If this project helped you, consider giving it a star!**

**💰 Personal Finance - Transforming Personal Financial Control**

**Made with ❤️ by [Patrick Batista](https://github.com/Patrickbpds)**

</div>
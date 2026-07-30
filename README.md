# Smart Inventory Management System (SIMS) — Backend

A full-featured, enterprise-style inventory management REST API built with **Spring Boot 3**, **Java 21**, and **PostgreSQL**. Built as an end-to-end learning project covering layered architecture, JWT authentication, role-based authorization, transactional business logic, and audit/notification systems.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

---

## ✨ Features

- **Authentication & Security** — JWT access + refresh tokens (with rotation), BCrypt password hashing, role-based authorization (`ADMIN`, `MANAGER`, `STAFF`), change/forgot/reset password flows
- **User Management** — Admin-managed user CRUD, enable/disable accounts, role assignment, search & pagination
- **Product Catalog** — Categories, Suppliers, and Products with full CRUD, search/filter, and image upload
- **Purchase Management** — Multi-item purchase orders, auto-generated purchase numbers, PENDING → RECEIVED lifecycle with automatic stock updates, editable while pending
- **Sales Management** — Multi-item sales with server-side pricing, stock validation (no overselling), invoice generation, cancellation with automatic stock reversal
- **Inventory Control** — Manual stock adjustments (manager-approved), full inventory transaction history/audit trail
- **Dashboard & Reporting** — KPI summary, monthly sales/purchase charts, low-stock alerts, inventory valuation report, supplier performance report, Excel (.xlsx) export
- **Audit Logging** — Automatic activity logging across all major write operations (who did what, when)
- **Notifications** — In-app + email notifications, automatic low-stock alerts to Admins/Managers
- **File Uploads** — Local product image storage with type/size validation

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 (LTS) |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT (jjwt) |
| Data Access | Spring Data JPA / Hibernate |
| Database | PostgreSQL 17 |
| Build Tool | Maven |
| Utilities | Lombok |
| Reporting | Apache POI (Excel export) |
| Email | Spring Boot Starter Mail |

## 🏗 Architecture

Layered architecture following clean separation of concerns:

```
Controller → Service → Repository → Database
     ↓           ↓
   DTOs      Entities
```

- **Controllers** — HTTP request/response handling only, no business logic
- **Services** — business rules, validation, transactions
- **Repositories** — Spring Data JPA data access
- **DTOs** — isolate the API contract from the persistence model
- **Global Exception Handler** — consistent, structured error responses across the API

## 📁 Package Structure

```
com.inventory
├── config          # Security, CORS, and web configuration
├── controller       # REST controllers
├── service           # Business interfaces
│     └── impl        # Business implementations
├── repository        # Spring Data JPA repositories
├── entity             # JPA entities
├── dto                # Request/response DTOs
├── security           # JWT filter, JWT utils, UserDetailsService
├── exception          # Global exception handler + custom exceptions
├── enums               # Status/type enumerations
└── InventoryApplication.java
```

## 🚀 Getting Started

### Prerequisites
- Java 21 (JDK)
- PostgreSQL 14+
- Maven (or use the included `./mvnw` wrapper)

### 1. Clone the repository
```bash
git clone https://github.com/dhanesh-lakshan/Smart_Inventory_Management_System_BackEnd.git
cd Smart_Inventory_Management_System_BackEnd
```

### 2. Set up the database
Create a PostgreSQL database:
```sql
CREATE DATABASE sims_db;
```
Run the schema scripts in `/sql` (or your saved `.sql` files) to create all required tables.

### 3. Configure environment
Copy the example properties file and fill in your own values:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```
Update the following in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sims_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_DB_PASSWORD

jwt.secret=YOUR_JWT_SECRET_MIN_32_CHARS
jwt.expiration-ms=86400000
jwt.refresh-expiration-ms=604800000

spring.mail.username=YOUR_GMAIL_ADDRESS
spring.mail.password=YOUR_GMAIL_APP_PASSWORD
app.email.enabled=false
```

> ⚠️ Never commit `application.properties` with real credentials. It is already excluded via `.gitignore`.

### 4. Run the application
```bash
./mvnw clean spring-boot:run
```
The API will start at `http://localhost:8080`.

## 📡 API Overview

Base path: `/api/v1`

| Module | Base Endpoint |
|---|---|
| Auth | `/auth/login`, `/auth/refresh`, `/auth/logout`, `/auth/change-password`, `/auth/forgot-password`, `/auth/reset-password` |
| Profile | `/profile/me` |
| Users (Admin) | `/users` |
| Categories | `/categories` |
| Suppliers | `/suppliers` |
| Products | `/products` |
| Purchases | `/purchases` |
| Sales | `/sales` |
| Inventory | `/inventory/adjust`, `/inventory/history` |
| Dashboard | `/dashboard`, `/dashboard/sales-chart`, `/dashboard/reports/*` |
| Notifications | `/notifications` |
| Audit Logs (Admin) | `/audit-logs` |

All endpoints (except `/auth/**`) require:
```
Authorization: Bearer <accessToken>
```

### Standard Response Format
```json
{
  "success": true,
  "message": "Human-readable description",
  "data": { },
  "timestamp": "2026-07-30T10:00:00Z"
}
```

## 🔐 Security Notes

- Passwords are hashed with BCrypt — never stored in plain text
- JWT secret is injected via environment variable in production (`JWT_SECRET`)
- All destructive/approval actions (delete, receive, cancel, stock adjustment) are role-restricted
- Every write operation is captured in the audit log

## 🗺 Roadmap / Not Yet Implemented

- Swagger / OpenAPI documentation
- Unit & integration test suite
- Docker containerization
- CI/CD pipeline
- Cloud file storage (AWS S3) for product images

## 📄 License

This project is available under the MIT License.

# Atlas RH

[![Java](https://img.shields.io/badge/Java-17-red.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![DB](https://img.shields.io/badge/PostgreSQL-17-blue.svg)](https://www.postgresql.org/)
[![Build](https://img.shields.io/badge/build-maven-lightgray.svg)](https://maven.apache.org/)

**Atlas RH** is a back-office Human Resources management system built with Spring Boot.
It allows the HR team to manage the company's core HR entities: **departments**, **employees**,
**contracts**, and **leave requests**, with role-based access control and a REST API.

> ⚠️ This project is **work in progress**. The entity and service layers are in place, but the
> application is not yet a fully finished product.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [1. Clone the repository](#1-clone-the-repository)
  - [2. Start the database (Docker Compose)](#2-start-the-database-docker-compose)
  - [3. Configure the environment](#3-configure-the-environment)
  - [4. Run the application](#4-run-the-application)
  - [5. Run the tests](#5-run-the-tests)
- [API](#api)
- [Authorization](#authorization)
- [Database](#database)
- [Configuration](#configuration)
- [Running with Docker](#running-with-docker)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- **Departments** — create and manage organizational departments.
- **Employees** — manage the employee list with roles and personal information.
- **Contracts** — manage employee contracts (CDI / CDD / STAGE) with salaries.
- **Leave requests** — manage leave requests with statuses and manager approval.
- **Role-based access control** — three roles control access to each feature:
  - `RH` (HR) — manages departments, contracts, and employees.
  - `MANAGER` — manages leave requests.
  - `EMPLOYE` (employee) — manages their own leave requests.
- **REST API** — Spring WebMVC endpoints under `/api...`.
- **Database migrations** — Liquibase changelog tracks all schema changes.
- **Email support** — Spring Mail is configured for future notifications.

## Tech Stack

| Layer          | Technology                                                              |
| -------------- | ----------------------------------------------------------------------- |
| Language       | Java 17                                                                 |
| Framework      | Spring Boot 4.1.1                                                       |
| Build          | Maven (parent POM, `mvnw`)                                              |
| Database       | PostgreSQL 17 (via Docker Compose)                                      |
| Migrations     | Liquibase                                                             |
| ORM            | Spring Data JPA                                                         |
| Mapping        | MapStruct + Lombok                                                      |
| Validation     | Lombok + Spring Boot starters                                           |
| Email          | Spring Mail                                                             |
| Container      | Docker / Docker Compose                                                 |

## Project Structure

```text
atlasrh/
├── src
│   ├── main
│   │   ├── java
│   │   │   └── ma/atlasrh/atlasrh/
│   │   │       ├── AtlasrhApplication.java
│   │   │       ├── contract/      # Contract entity, DTO, mapper, repository, service
│   │   │       ├── departement/   # Department entity, DTO, mapper, repository, service, controller
│   │   │       ├── employee/      # Employee entity, Role enum, repository, service, exceptions
│   │   │       ├── leave/         # LeaveRequest entity, LeaveType enum, repository, service
│   │   │       └── ...            # (mappings, controllers, config...)
│   │   └── resources
│   │       ├── application.yaml
│   │       └── db/changelog/
│   │           ├── db.changelog-master.yaml
│   │           └── changes/
│   │               ├── 001-create-users.yaml
│   │               ├── 002-create-roles.yaml
│   │               ├── ...        # per-entity migration files
│   └── test
│       └── java/...               # Spring Boot integration tests
├── compose.yaml
├── Dockerfile
├── pom.xml

## Getting Started

### Prerequisites

- **JDK 17** or later (Temurin / Eclipse Temurin recommended)
- **Maven 3.9+** (the wrapper `./mvnw` is included in the repository, so it is optional)
- **Docker & Docker Compose** (to run the PostgreSQL database locally)

### 1. Clone the repository

```bash
git clone https://github.com/ZiyadCh/atlasrh.git
cd atlasrh
```

### 2. Start the database (Docker Compose)

```bash
docker compose up -d
```

This starts a PostgreSQL 17 container with:

| Variable     | Value     |
| ------------ | --------- |
| Database     | `atlasrh` |
| User         | `kaiser`  |
| Password     | `kaiser` (see `.env`) |
| Port (host)  | `5433`    |
| Port (container) | `5432` |

The volume `pgdata` is used to persist data across restarts.

### 3. Configure the environment

Copy the sample environment file (if provided) and adjust it, or create a `.env` at the project root:

```properties
DB_PASSWORD=kaiser
```

The application loads this file automatically via Spring's `spring.config.import` property.

### 4. Run the application

Using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

Or, if you have Maven installed:

```bash
mvn spring-boot:run
```

The application starts on port **8081** (`server.port: 8081`) and automatically runs
Liquibase migrations on startup.

└── .env                           # Database passwords


### 5. Run the tests

```bash
./mvnw test
```

## API

The API is currently built around REST endpoints under `/api`. All secured endpoints
require the `user-id` header (the ID of the authenticated employee).

| Method | Path                  | Description                   | Role required       |
| ------ | --------------------- | ----------------------------- | ------------------- |
| POST   | `/api/departement`    | Create a department           | `RH`                |

> ℹ️ The API surface is **incomplete** and more endpoints are planned (employees,
> contracts, leave requests).

Request body example for creating a department:

```json
{
  "nom": "Ressources Humaines",
  "description": "The HR department"
}
```

## Authorization

The `AuthorizationService` enforces role-based permissions. It is used by the
services to deny access to users who do not have the required role:

| Feature             | Required role |
| ------------------- | ------------- |
| Departments         | `RH`          |
| Contracts           | `RH`          |
| Employees           | `RH`          |
| Leave requests        | `MANAGER`     |
| Employee's own leaves | `EMPLOYE`   |

Unprivileged requests throw a `NoAccessException`.

## Database

The database schema is managed with **Liquibase**. All changelog files live in
`src/main/resources/db/changelog/`:

- `db.changelog-master.yaml` — the master change log that includes the per-entity
  change sets.
- `changes/001-create-users.yaml`, `changes/002-create-roles.yaml`, etc. — one file
  per entity.

The first run of the application creates the schema automatically. If you need to
reset the database, stop the container and run:

```bash
docker compose down -v   # deletes the data volume
docker compose up -d
```

## Configuration

The main configuration lives in `src/main/resources/application.yaml`:

| Property | Default        | Description                               |
| -------- | -------------- | ----------------------------------------- |
| `server.port` | `8081`    | HTTP port                                 |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5433/atlasrh` | PostgreSQL connection |
| `spring.datasource.username` | `kaiser` | DB user                        |
| `spring.datasource.password` | `${DB_PASSWORD:changeme}` | DB password (from `.env`) |
| `spring.liquibase.change-log` | `classpath:db/changelog/db.changelog-master.yaml` | Migration file |

## Running with Docker

A Dockerfile is included to build and run the application as a container:

```bash
# Build the image
docker build -t atlasrh .

# Run the container (database must be reachable at localhost:5433)
docker run -p 8081:8081 --env-file .env atlasrh
```

## Contributing

1. Fork the repository and create a feature branch (`AT-xx` tasks are tracked locally).
2. Write code following the existing conventions (Lombok, MapStruct, clean layered structure).
3. Add/adapt Liquibase migrations when the database schema changes.
4. Add tests for new logic when possible.
5. Open a pull request against `master`.

## License

This project is unlicensed as of now. Copyright (c) 2026 Atlas RH.

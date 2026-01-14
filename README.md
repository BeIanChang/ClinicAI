# ClinicAI – Phase III: Integrated System

CAS 735 – Fall 2025 – McMaster University  
Phase 3: Integration (10%)

This repository contains the **integrated ClinicAI system** used for the Phase 3 assignment.  
The system is composed of multiple microservices connected through **RabbitMQ** and backed by **MongoDB**.

The architecture is deployable locally via **Docker Compose** and can also be run directly from the command line.

---

## 1. Services & Responsibilities

| Service             | Tech Stack               | Default Port | Responsibility |
|---------------------|--------------------------|--------------|----------------|
| `encounter`        | Java 21, Spring Boot, Maven | `8087`       | Manage schedules, availability, appointments, and encounters; publish domain events. |
| `billing`          | Java 21, Spring Boot, Maven | `8082`       | Create invoices for encounters / lab tests, simulate payment authorization, publish payment events. |
| `data`             | Java 21, Spring Boot, Maven, MongoDB | `8083` | Store analytic reports per encounter and coordinate with a (mock) data analysis partner. |
| `labtest-list`     | Java 17, Spring Boot, Gradle | `8085`     | Maintain lab test lists per encounter; allows clinicians to update requested tests. |
| `account` | Java 17, Spring Boot, Gradle | `8081`     | Manage accounts and publish account-registered events (used by email). |
| `labtest`          | Java 17, Spring Boot, Gradle | `8088`     | Manage individual lab tests and publish test-completed events. |
| `email`            | Java 17, Spring Boot, Gradle | `8084`     | Consume email-related events (welcome emails, lab notifications). |

Message bus: **RabbitMQ topic exchange** `clinicai.events` (plus service-specific exchanges where applicable).  
Persistence: **MongoDB** (e.g., reports, lab lists, etc.).

---

## 2. Technical Dependencies

### 2.1 System Requirements

- **JDK**: 17 & 21
- **Build tools**:
  - Maven 3.9+ (`encounter`, `billing`, `data`)
  - Gradle 8+ (`labtest`, `labtest-list`, `account`, `email`)
- **Docker & Docker Compose**
- **RabbitMQ** (3.x with management plugin, provided by `docker-compose.yml`)

All Java dependencies (Spring Boot, AMQP, MongoDB driver, OpenAPI, etc.) are declared in each service’s own build file.

---

## 3. Running the System (Docker Compose)

The easiest way to run the integrated system is with Docker Compose from the repository root.

### 3.1 Start infrastructure & services

```bash
docker compose up --build
```

This will:

- Start **RabbitMQ** (ports `5672`, `15672`).
- Build and run all microservices (`encounter`, `billing`, `data`, `labtest-list`, `account`, `labtest`, `email`).

RabbitMQ management UI: `http://localhost:15672` (user: `guest`, password: `guest`).

To stop everything:

```bash
docker compose down
```

---

## 4. Running Services from the Command Line (without Docker)

You can also run individual services directly from the CLI (useful during development).  
Make sure you have **RabbitMQ** and **MongoDB** running locally (e.g., using Docker):

```bash
# RabbitMQ
docker run -d --name rabbit \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:3-management
```

### 4.1 Encounter Service

```bash
cd encounter
./mvnw -DskipTests spring-boot:run
```

Default URL: `http://localhost:8087`  
OpenAPI / Swagger UI (if enabled for the module): `http://localhost:8087/swagger-ui.html`

### 4.2 Billing Service

```bash
cd billing
./mvnw -DskipTests spring-boot:run
```

Default URL: `http://localhost:8082`

### 4.3 Data Service

```bash
cd data
./mvnw -DskipTests spring-boot:run
```

Default URL: `http://localhost:8083`

### 4.4 Lab Test List Service

```bash
cd labtest-list
./gradlew bootRun
```

Default URL: `http://localhost:8085`

### 4.5 Other Services

Similarly, from the repository root:

```bash
cd account && ./gradlew bootRun
cd email             && ./gradlew bootRun
cd labtest           && ./gradlew bootRun
```

> When running locally without Docker Compose, ensure that every service uses the same RabbitMQ host/port and MongoDB connection, as configured in their respective `application.yml`.

---

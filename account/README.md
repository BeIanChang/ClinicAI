
# 🏥 ClinicAI Account Management Service

This service is the **account management** component for ClinicAI, handling core functions like **registration, login, and sending welcome emails**. It is built using **Spring Boot** and adheres to **Hexagonal / Clean Architecture** principles, organizing code into `business`, `ports`, `adapters`, and `dto` layers for maintainability and testability.


## 🛠️ Technical Details & Architecture

### Dependencies

* **Java 17+** (tested on OpenJDK 17)
* **Spring Boot 3.x**
* **Gradle** (recommended for building)
* **Spring AMQP** (for messaging / RabbitMQ integration)
* **Spring Web** (for REST API support)
* **Spring Boot Test** (for testing utilities, JUnit, Mockito, etc.)

## 🚀 Getting Started

### Prerequisites

You need **Java 17+** installed on your system.

### Running the Service

1.  **Clone the repository:**
    ```bash
    git clone <repo-url>
    cd account
    ```

2.  **Build and Run with Gradle:**
    ```bash
    ./gradlew build
    ./gradlew bootRun
    ```
    The service will start and be accessible at `http://localhost:8081`.


## ⚠️ Notes & Extensibility

  * **Data Storage**: Accounts are stored in an **in-memory HashMap** (`InMemoryAccountRepository`). All data is **lost** when the service is restarted.
  * **Email Sending**: Email functionality is **mocked** (`MockEmailSender`) and only prints the welcome message to the console.
  * **Security**: Passwords are currently stored in **plain text**. **MUST** be encrypted (hashed) for any production deployment.
  * **Swapping Adapters**: The Hexagonal Architecture makes it easy to replace the current mock adapters. For instance, you can swap `InMemoryAccountRepository` with a **JPA** or **MongoDB** adapter, and `MockEmailSender` with a **real email service** (e.g., SendGrid, AWS SES) implementation.
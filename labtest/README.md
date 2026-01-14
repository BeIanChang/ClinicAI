# 💉 ClinicAI Lab Test Management Service

This service is dedicated to managing the **complete lifecycle of a patient's lab test**, covering scheduling, confirmation, progress tracking, and notifying other services via asynchronous events upon key status changes.

It is implemented using **Spring Boot** and strictly adheres to the **Hexagonal / Clean Architecture** principles, with **Spring AMQP (RabbitMQ)** handling asynchronous communication.


## 🛠️ Technical Details & Architecture

### Key Functions

1.  **Lab Test Lifecycle:** Tracks test status through states: `SCHEDULED`, `CONFIRMED`, `IN_PROGRESS`, and `COMPLETED`.
2.  **Event-Driven Communication:** Publishes and consumes events using RabbitMQ to integrate with other ClinicAI microservices (e.g., notifying the Email service or the Report Upload service).

### Dependencies

  * **Java 17+**
  * **Spring Boot 3.x**
  * **Gradle** (Build Tool)
  * **Spring Web** (REST API Support)
  * **Spring AMQP** (RabbitMQ Integration)

## 🚀 Getting Started

### Prerequisites

1.  **Java 17+** must be installed.
2.  A running **RabbitMQ instance** is required for full messaging functionality.

### Running the Service

1.  **Clone the repository and navigate to the directory:**

    ```bash
    git clone <repo-url>
    cd labtest
    ```

2.  **Build and Run using Gradle:**

    ```bash
    ./gradlew build
    ./gradlew bootRun
    ```

    The service will typically start at `http://localhost:8080`.
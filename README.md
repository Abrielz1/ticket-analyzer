# Ticket Analyzer CLI Platform

# Задание

Напишите программу на языке программирования
java, которая прочитает файл tickets.json и
рассчитает:
- Минимальное время полета между городами
  Владивосток и Тель-Авив для каждого
  авиаперевозчика
- Разницу между средней ценой и медианой для
  полета между городами  Владивосток и Тель-Авив
  Программа должна вызываться из командной строки
  Linux, результаты должны быть представлены в
  текстовом виде.
  В качестве результата нужно прислать ответы на
  поставленные вопросы и ссылку на исходный код.

## 1. Overview

A production-ready, console-based Java application for analyzing airline ticket data. This tool is built with a robust, modern technology stack including Spring Boot, Picocli, and MongoDB, showcasing a clean, scalable, and testable architecture.

The application is designed to calculate two key metrics for a given flight route (e.g., Vladivostok to Tel Aviv):
1.  The minimum flight duration for each airline carrier.
2.  The difference between the average and median ticket prices for the specified route.

## 2. Core Features & Architecture

*   **Clean Architecture:** The project strictly separates concerns into Domain, Service, Utility, and Presentation layers. Dependencies flow inwards towards the pure Domain Model.
*   **Domain-Driven Design (DDD):** Utilizes immutable `record` classes as Value Objects (`Price`, `GeoPoint`, `AirportInfo`, etc.) for a rich, type-safe, and self-validating domain model.
*   **Robust CLI:** Built with Picocli for professional, user-friendly argument parsing, validation, and auto-generated help (`--help`, `--version`).
*   **Intelligent Data Pipeline:** Implements a multi-layered, fault-tolerant data sourcing strategy:
    1.  **User Override:** Reads from a user-provided file if specified.
    2.  **Persistent Cache:** Falls back to a MongoDB cache for subsequent runs.
    3.  **Cold Start:** Loads a default internal JSON file if the cache is empty.
*   **Industrial-Grade Infrastructure:** Comes with a multi-stage `Dockerfile` for lean production images and a `docker-compose.yml` for an easy, reproducible local setup, including a MongoDB service with health checks.
*   **"Paranoid" Build:** The `pom.xml` is configured with the Maven Enforcer Plugin to prevent dependency conflicts and ensure build consistency.

## 3. Prerequisites

*   Java 17 or higher
*   Apache Maven 3.8+
*   Docker and Docker Compose

## 4. How to Build

The project is built using Maven. The `spring-boot-maven-plugin` will create a single, executable "fat JAR".

From the project root directory, run:
```bash
mvn clean package
```

## 5. How to Run

The recommended way to run the application is with Docker Compose, as it manages both the application and its database dependency in a single, reproducible environment.

### 5.1. Using Docker Compose (Recommended)

This method is ideal for a stable, development-like setup.

1.  (Optional) Prepare Your Data:** If you wish to use a custom data file, place it in the `input_data/` directory at the project root. The application will use this file on its first run to populate the database.

2.  Build and Run:** From the project root, execute the following command:

        docker-compose up --build

3.  The application will start, wait for MongoDB to be healthy, and then run the analysis using the arguments specified in the `command` section of the `docker-compose.yml` file.

    > **Note: To analyze a different route or use a different file on subsequent runs with `docker-compose`, you must modify the `command` section in the `docker-compose.yml` file.

### 5.2. Using `docker run` (For Ad-Hoc Analysis)

This method is perfect for running the tool like a true command-line utility against various data files without modifying any configuration.

1.  Build the Docker Image:** First, build the image once:

        docker build -t ticket-analyzer .

2.  Run the Analysis:** You can now run the tool against any file on your system.

        # Example: Running against a file in your home directory
        docker run --rm -v "/home/user/my-data:/app/data" ticket-analyzer /app/data/tickets.json "Владивосток" "Тель-Авив"

      The `-v` flag mounts your local data directory into the container's `/app/data` directory.

### 5.3. Running the JAR Directly (For Quick Tests)

You can also run the compiled JAR file directly, which is useful for quick, local tests if you have MongoDB running separately.

1.  Build the JAR:

        mvn clean package

2.  Run with an external file:

        java -jar target/ticket-analyzer-1.0.0.jar /path/to/your/tickets.json "Владивосток" "Тель-Авив"

3.  Run in "cold start" mode** (using the internal fallback JSON to populate the DB):

        # The application will automatically detect that no file path was given and use its pipeline.
        java -jar target/ticket-analyzer-1.0.0.jar "Владивосток" "Тель-Авив"

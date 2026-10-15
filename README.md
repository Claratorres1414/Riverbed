# spring-bootstrap

Reusable boilerplate for Java and Spring Boot applications.

## Stack

- Java 21
- Spring Boot
- Gradle
- PostgreSQL
- Redis
- Docker
- Docker Compose

## Requirements

- Java 21
- Docker
- Docker Compose

Gradle does not need to be installed because the project uses the Gradle Wrapper.

## Getting Started

Clone the repository and configure the environment:

    git clone <repository-url>
    cd spring-bootstrap
    cp .env.example .env

Edit `.env` with the required environment variables.

Example:

    APP_NAME=spring-bootstrap
    SERVER_PORT=8080

    DB_HOST=localhost
    DB_PORT=5432
    DB_NAME=spring_bootstrap
    DB_USERNAME=postgres
    DB_PASSWORD=postgres

    REDIS_HOST=localhost
    REDIS_PORT=6379

The `.env` file contains local configuration and must never be committed. The `.env.example` file must be committed and kept synchronized with the variables required by the application.

Required environment variables do not use fallback values. Missing variables should cause the application to fail during startup.

## Running Locally

Start PostgreSQL and Redis:

    docker compose up postgres redis

Then run the application:

    ./gradlew bootRun

The `dev` profile is used by default.

When running Spring Boot directly from the host:

- PostgreSQL → `localhost:5432`
- Redis → `localhost:6379`

## Running with Docker Compose

Build and start the complete environment:

    docker compose up --build

This starts:

- Spring Boot
- PostgreSQL
- Redis

After the image has already been built:

    docker compose up

Run in background:

    docker compose up -d

Stop the environment:

    docker compose down

Stop the environment and remove persistent volumes:

    docker compose down -v

> Removing volumes deletes the persisted PostgreSQL data.

Inside the Docker network, services communicate using their Compose service names:

- PostgreSQL → `postgres:5432`
- Redis → `redis:6379`

## Docker

The application uses a multi-stage Docker build.

The build stage uses Eclipse Temurin 21 JDK and the runtime stage uses Eclipse Temurin 21 JRE.

The final image runs the application as a non-root user.

### Build the image manually

    docker build -t spring-bootstrap .

### Run the image directly

    docker run --rm \
      -p 8080:8080 \
      -e APP_NAME=spring-bootstrap \
      -e SERVER_PORT=8080 \
      spring-bootstrap

Docker Compose is recommended for normal development because it provides the complete application environment.

## Profiles

The project provides the following Spring profiles:

- `dev`
- `prod`

The default profile is `dev`.

Profiles are reserved for configuration and behavior that are genuinely environment-specific.

Infrastructure connection details are provided through environment variables rather than being hardcoded into profiles. This allows the same application configuration to work with local, Docker, or managed infrastructure.

## Configuration Philosophy

Configuration is externalized through environment variables.

Common application configuration is defined in `application.properties`, while environment-specific behavior can be defined through Spring profiles.

Required environment variables do not define fallback values.

Example:

    spring.application.name=${APP_NAME}

Instead of:

    spring.application.name=${APP_NAME:default-name}

The application should not need to change when the infrastructure provider changes. Only the environment variables need to change.

## Infrastructure

### PostgreSQL

PostgreSQL is provided through Docker Compose for local development.

The database uses the persistent Docker volume:

    postgres_data

Running:

    docker compose down

does not remove the database data.

To remove the database data:

    docker compose down -v

### Redis

Redis is provided through Docker Compose for local development.

Redis is currently treated as ephemeral infrastructure and does not use a persistent volume.

### Health Checks

Docker Compose uses health checks for PostgreSQL and Redis.

PostgreSQL is considered healthy when `pg_isready` confirms that the database is accepting connections.

Redis is considered healthy when it responds to `PING`.

The application waits for PostgreSQL and Redis to become healthy before starting.

## Project Structure

    spring-bootstrap/
    ├── .dockerignore
    ├── .editorconfig
    ├── .env
    ├── .env.example
    ├── .gitignore
    ├── Dockerfile
    ├── README.md
    ├── compose.yaml
    ├── build.gradle
    ├── settings.gradle
    ├── gradlew
    ├── gradlew.bat
    ├── config/
    └── src/
        └── main/
            ├── java/
            └── resources/
                ├── application.properties
                ├── application-dev.properties
                └── application-prod.properties

## Git

The following files and directories must not be committed:

    .env
    .gradle/
    build/
    .idea/
    *.iml

The `.env.example` file must always reflect the environment variables required by the current version of the project.

## Current Scope

This phase provides the initial application and infrastructure foundation.

The following features are intentionally outside the current scope:

- Authentication
- Authorization
- JWT
- JPA
- Entities
- Flyway
- Business rules
- Global exception handling
- OpenAPI
- Application security
- Automated tests
- CI/CD

## Roadmap

Future phases will introduce:

- Database migrations with Flyway
- BaseEntity
- Auditing
- Global exception handling
- OpenAPI
- Security
- JWT
- User management
- Automated tests
- CI/CD
- Code quality tooling# Riverbed

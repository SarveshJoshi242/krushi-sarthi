# Krushi-Adhaar

Project identity: **Krushi-Adhaar**

## Requirements

* JDK 17
* Gradle 8+
* Git
* Neon PostgreSQL connection string

## Environment Setup

The database connection must be provided via the `DATABASE_URL` environment variable.
Do not commit real credentials.

To set up your local environment:
1. Copy `.env.example` to `.env` in the `backend/` directory.
2. Edit `.env` and set `DATABASE_URL` to your real Neon connection string.

Example:
`DATABASE_URL=jdbc:postgresql://your_neon_host.aws.neon.tech/krushi_adhaar?sslmode=require`

## Run Backend

To run the backend application:
```bash
./gradlew bootRun
```

## Test Backend

To run tests:
```bash
./gradlew test
```
*Note: Make sure your `.env` is configured with a valid database url, as Spring tests load the application context.*

## Health Check

The backend exposes health endpoints:
* `GET /api/health` - Basic health check
* `GET /api/health/db` - Verifies database connectivity

## Database

This project uses **Neon PostgreSQL** and **Flyway** for database migrations. Migrations are stored in `src/main/resources/db/migration`.

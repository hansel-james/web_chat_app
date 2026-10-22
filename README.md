# Web Chat App

## Phase 0: local foundation

Requirements: Java 21, Node.js 22+, npm, and Docker Compose.

1. Start local PostgreSQL from the repository root:

   ```powershell
   docker compose up -d postgres
   ```

   Kafka is optional until the asynchronous messaging phase:

   ```powershell
   docker compose --profile messaging up -d kafka
   ```

2. In `backend`, copy `.env.example` to `.env` only if you do not already have a local `.env`. Keep real credentials out of Git. The sample values are for local development only. If you already have a `.env`, update it without replacing your credentials: use the local Compose database, use a JDBC URL in `POSTGRESQL_CONNECTION` (`jdbc:postgresql://localhost:5432/webchat`), provide username and password separately, and set `FLYWAY_ENABLED=true`. Do not point this initial migration at an existing or production database.
3. Start the backend from `backend`; Flyway applies versioned migrations and Hibernate validates the resulting schema:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

4. Start the React development server:

   ```powershell
   cd frontend
   npm install
   npm run dev
   ```

The entity tests use H2 by default. CI runs them against a fresh, isolated PostgreSQL service so Flyway migrations and Hibernate schema validation are tested with the PostgreSQL dialect. Flyway is disabled by default in the application; enable it explicitly only for a fresh local database or after reviewing a migration and arranging backups for an existing TEST or PROD database.

## CI

GitHub Actions verifies the backend against PostgreSQL and builds the frontend on pushes and pull requests.

## Branches

Use short-lived feature branches and pull requests into `main`. Keep TEST and PROD credentials in separate protected GitHub environments; never expose PROD secrets to feature-branch builds.

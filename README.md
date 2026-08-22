# Fleetmanagement

WanderCar (internal codename **Fleman**) is a full-stack car rental / fleet management system: a React frontend backed by two functionally-equivalent REST APIs — a Spring Boot (Java) backend and an ASP.NET Core (.NET) port of the same domain logic. Either backend can run the frontend unmodified.

## Repository layout

```
WanderCar/
  WanderCar_Frontend/   React 19 + Vite + Tailwind SPA
  WanderCar_JAVA/       Spring Boot 4.0 backend (Java 17,language Java 8)
  WanderCar_DotNet/     ASP.NET Core 8 backend (port of the Java API)
```

Each subfolder has its own README with full setup details; this file covers the project as a whole.

## Architecture

- **Frontend** — React 19, React Router, Tailwind CSS, Axios. Talks to whichever backend is running via `VITE_API_BASE_URL` (`/api`, proxied).
- **Java backend** (`WanderCar_JAVA`) — Spring Boot 4.0, Spring Data JPA, Spring Security (BCrypt), JJWT, MySQL . Runs on port `8080`.
- **.NET backend** (`WanderCar_DotNet`) — ASP.NET Core 8, EF Core (Pomelo MySQL), FluentValidation, AutoMapper, JWT + Google OAuth. Runs on port `5180`/`7156`. Both APIs expose the same route shapes, so the frontend needs no changes to switch between them.

Core domain: states/cities/hubs/airports, car types & vehicles, add-ons, customers, bookings (create/modify/cancel), staff handover/return with invoicing, and JWT-based auth (including Google OAuth on the .NET side).

## Getting started

### 1. Frontend

```bash
cd WanderCar_Frontend
npm install
npm run dev
```

### 2. Backend — pick one

**Java** (zero-setup, embedded DB):

```bash
cd WanderCar_JAVA
mvn spring-boot:run
```

**.NET** (requires local MySQL):

```bash
cd WanderCar_DotNet/src/FlemanApi
dotnet run
```

See each backend's own README for database setup, environment variables, and test commands.

## ⚠️ Before pushing to GitHub

Some files in this project currently contain **real credentials** (SMTP password, Google OAuth client secret) and are only kept out of git via `.gitignore`:

- `WanderCar_JAVA/src/main/resources/application.properties`
- `WanderCar_DotNet/src/FlemanApi/appsettings.Development.json`

Double-check these are not force-added or already tracked before your first commit (`git status --ignored`), and rotate any secrets that may have already been shared or committed elsewhere. Use `appsettings.json` / `application-*.properties` templates with blank values for anything checked into the repo, and supply real values via environment variables, user-secrets, or a local untracked file instead.

## Tech stack summary

| Layer | Stack |
|---|---|
| Frontend | React 19, Vite, Tailwind CSS 4, React Router 7, Axios |
| Backend (Java) | Spring Boot 4.0, Spring Data JPA, Spring Security, JJWT, MySQL/H2 |
| Backend (.NET) | ASP.NET Core 8, EF Core, FluentValidation, AutoMapper, MySQL |
| Auth | JWT (both backends), Google OAuth2 (.NET) |

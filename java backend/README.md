# FLEMAN Backend — Full API (Spring Boot)

A complete Spring Boot backend for the FLEMAN/WanderR fleet-rental frontend,
built to match `frontend/src/data/mockDb.js` and `frontend/src/api/*.js`
field-for-field so the existing React app can be pointed at it with no
component-level changes — only the `src/api/*.js` files change.

## Running it

    mvn spring-boot:run

Starts on :8080. Uses an embedded, file-based H2 database (`./data/fleman`) —
no setup required. Seed data (identical to the frontend's mockDb.js, including
the sample `WDR-2894` booking) loads automatically on first run.

To point at MySQL instead, edit `application.properties` — the block is
commented in, ready to uncomment.

## What's included

- 12 entities: State, City, Hub, Airport, CarType, Car, Addon, Customer,
  BookingHeader, BookingDetail, InvoiceHeader, InvoiceDetail
- Full CRUD + business logic across 6 services: location lookups, vehicle
  availability (custom overlap-check query, same pattern as the earlier
  FLEMAN Home Page work), add-ons, customer/auth, booking lifecycle, and the
  staff handover/return-with-invoice flow
- Real BCrypt password hashing and real signed JWTs (see `JwtUtil` and
  `SecurityConfig` for the documented scope decision on why endpoints are
  still open — the current frontend never sends a token to check)
- No Lombok anywhere — every entity/DTO has explicit constructors, getters,
  and setters

## One corrected mismatch

The frontend's `locationService.js` comments `searchAirports()` as
`GET /api/hubs/search?q=` even though it searches the airports list. This
backend implements the correct `GET /api/airports/search?q=` instead — the
matching frontend update points at the fixed path.

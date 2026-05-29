# Spring Boot REST API

A production-style REST API built with Java and Spring Boot, demonstrating clean
three-layer architecture, JWT authentication, transaction management, validation,
global exception handling, and unit testing. Built as a backend engineering
portfolio project.

## Why this project

The domain is intentionally small (a book management API) but the project is built
to production standards. It demonstrates the patterns I apply in real backend work:
separation of concerns, stateless JWT authentication, defensive error handling,
transactional integrity, and tested business logic. It also includes a deliberate
demonstration of a common Spring pitfall (see "The @Transactional proxy bypass"
below).

## Architecture

The application follows a strict three-layer architecture, where each layer has a
single responsibility:

- **Controller** — handles HTTP concerns only: request parsing, input validation
  via `@Valid`, delegation to the service, and returning the correct HTTP status.
  No business logic, no data access.
- **Service** — holds business logic and transaction boundaries (`@Transactional`).
  The only layer that makes business decisions.
- **Repository** — data access only, via Spring Data JPA. Returns domain objects.

```
Controller (HTTP)  ->  Service (business logic + @Transactional)  ->  Repository (data)
```

## Features

- **JWT authentication** — stateless login flow issuing signed JSON Web Tokens; a
  custom `OncePerRequestFilter` validates the token on every request and populates
  the Spring `SecurityContext`. Sessions are stateless, so the API scales
  horizontally.
- Full CRUD REST API following REST conventions (GET, POST, PUT, DELETE) with
  correct status codes (200, 201, 204, 400, 401, 404)
- Bean validation with `@Valid`, `@NotBlank`, `@Size`
- Centralised error handling via `@RestControllerAdvice` returning consistent JSON
  error responses (timestamp, status, message, field errors)
- Custom `ResourceNotFoundException` returning clean 404s
- `NoResourceFoundException` handler so unmatched routes return 404 instead of a
  misleading 500
- Stream-based aggregations (filter by author, sorted titles, group-and-count)
- Unit tests with JUnit 5 and Mockito following the Arrange-Act-Assert pattern

## Authentication flow

1. `POST /auth/login` with username and password returns a signed JWT.
2. The client sends the token on subsequent requests as
   `Authorization: Bearer <token>`.
3. A custom filter (`JwtAuthenticationFilter extends OncePerRequestFilter`) runs on
   every request, extracts the token, verifies its signature and expiry, and on
   success sets the authentication into `SecurityContextHolder`.
4. Security is configured `STATELESS` — the token is the only source of identity;
   there is no server-side session.

The signature is created by signing the encoded header and payload with a secret
key (HMAC). If the payload is tampered with, the signature no longer matches and
the token is rejected.

## The @Transactional proxy bypass (demonstration)

Spring implements `@Transactional` using AOP via a dynamic proxy. When a method is
called from outside the bean, the proxy intercepts it and manages the transaction.
But when a method calls another `@Transactional` method **inside the same class**
(via `this`), the call bypasses the proxy and the transaction logic never fires.

This project includes endpoints that demonstrate this:

- `POST /books/test-gotcha` — an internal same-class call to a `@Transactional`
  method. Despite throwing an exception after save, the data is **not** rolled back,
  because the proxy was bypassed.
- `POST /books/test-fix` — the same logic, but the `@Transactional` method lives in
  a separate Spring bean. Now the call goes through the proxy and the rollback works
  correctly.

This is a common real-world Spring bug and a frequent interview question.

## API Endpoints

### Auth
| Method | Path         | Description                          | Auth |
|--------|--------------|--------------------------------------|------|
| POST   | /auth/login  | Authenticate and receive a JWT       | No   |

### Books (require a valid JWT)
| Method | Path                       | Description                       |
|--------|----------------------------|-----------------------------------|
| GET    | /books                     | List all books                    |
| GET    | /books/{id}                | Get a book by ID (404 if missing) |
| POST   | /books                     | Create a book (201, validated)    |
| PUT    | /books/{id}                | Update a book (404 if missing)    |
| DELETE | /books/{id}                | Delete a book (204 / 404)         |
| GET    | /books/by-author/{author}  | Filter books by author (stream)   |
| GET    | /books/titles              | Sorted list of titles (stream)    |
| GET    | /books/grouped-by-author   | Count of books per author (stream)|

## Tech Stack

| Layer       | Technology                                      |
|-------------|-------------------------------------------------|
| Language    | Java 17                                         |
| Framework   | Spring Boot 3, Spring Web, Spring Data JPA      |
| Security    | Spring Security, JWT (jjwt)                      |
| ORM         | Hibernate / JPA                                 |
| Database    | H2 (in-memory)                                  |
| Validation  | Jakarta Bean Validation                         |
| Testing     | JUnit 5, Mockito                                |
| Build       | Maven                                           |

## Running locally

Prerequisites: Java 17+, Maven.

```bash
# Clone
git clone https://github.com/Pramish17/spring-boot-rest-api.git
cd spring-boot-rest-api

# Run
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

### Try it

```bash
# 1. Log in to get a token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# 2. Use the token to call a protected endpoint
curl http://localhost:8080/books \
  -H "Authorization: Bearer <token-from-step-1>"
```

## Running tests

```bash
./mvnw test
```

Unit tests cover the service layer using Mockito to mock the repository, isolating
business logic from the database.

## What I'd add next

- Refresh tokens with short-lived access tokens
- Role-based authorisation via a `roles` claim in the JWT and `@PreAuthorize`
- Integration tests with `@SpringBootTest` and Testcontainers
- Pagination and sorting on list endpoints
- PostgreSQL via Docker instead of H2
- CI pipeline (GitHub Actions) running tests on every push
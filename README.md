# Spring Boot REST API

A production-style REST API built with Java and Spring Boot, demonstrating clean
three-layer architecture, transaction management, validation, global exception
handling, and unit testing. Built as a backend engineering portfolio project.

## Why this project

This project is intentionally small in domain (a book management API) but built to
production standards. It demonstrates the patterns I apply in real backend work:
separation of concerns, defensive error handling, transactional integrity, and
tested business logic. It also includes a deliberate demonstration of a common
Spring pitfall (see "The @Transactional proxy bypass" below).

## Architecture

The application follows a strict three-layer architecture, where each layer has a
single responsibility:

- **Controller** — handles HTTP concerns only: request parsing, input validation
  via `@Valid`, delegation to the service, and returning the correct HTTP status.
  No business logic, no data access.
- **Service** — holds business logic and transaction boundaries (`@Transactional`).
  The only layer that makes business decisions.
- **Repository** — data access only, via Spring Data JPA. Returns domain objects.

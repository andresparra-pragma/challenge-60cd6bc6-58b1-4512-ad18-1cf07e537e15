# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repo is

A Pragma training challenge ("Implementación de un Sistema de Gestión de Biblioteca con OOP", junior-l2, Java backend). `README.md` describes the phases and evaluation dimensions. `AGENTS.md` / `PROMPT_MEJORA.md` (identical) are the Fase 0 setup prompt; they say the setup assistant must not solve Fases 1–3, but the participant (repo owner) has since asked for the solution, and it is implemented here. Phase deliverables live in `docs/` (`diagrama-clases.md` for Fase 1, `decisiones.md` answering the evaluated dimensions) — keep them in sync with the code.

## Build & run

Use the Maven Wrapper (Maven is not installed globally). Java 17+.

```bash
./mvnw clean compile
./mvnw spring-boot:run                     # port 8080
./mvnw test
./mvnw test -Dtest=LoanServiceTest#returnBookReleasesBookAndUserQuota   # single test
```

## Architecture

Spring Boot app in `com.library`, layered `controller → service → repository → model`, all in memory (no DB; state is lost on restart).

- **model** — `Book` (id = title), `User` (id = email), `Loan` (id = sequential `Long`, `LoanStatus` ACTIVE/RETURNED). All implement `Identifiable<ID>`. No public setters: constructors validate via package-private `Validations` and state changes go through intent methods (`markAsLoaned`, `registerLoan`, `markAsReturned`), which throw domain exceptions on invariant violations.
- **repository** — generic `Repository<T extends Identifiable<ID>, ID>` + `InMemoryRepository` (synchronized `LinkedHashMap`); `BookRepository`/`UserRepository`/`LoanRepository` are empty subclasses annotated with the fully-qualified `@org.springframework.stereotype.Repository` (name clashes with the local interface).
- **service** — lookups throw `*NotFoundException` instead of returning `null`. `LoanService.borrowBook`/`returnBook` are `synchronized` and check every rule *before* mutating book/user so a rejected loan leaves no partial state. Dates come from an injected `Clock` bean (`config/ClockConfig`) so tests fix the date.
- **controller** — `LibraryController` under `/library`; request bodies are records in `controller/dto` that build entities (so entity validation runs on input). `GlobalExceptionHandler` maps exception *families*: `ResourceNotFoundException` → 404, `BusinessRuleException`/`DuplicateResourceException` → 409, `InvalidDataException` and Spring binding errors → 400, anything else → 500 with `ErrorResponse` body. New domain exceptions should extend the right family rather than add handlers.

# Fase 1 — Diagrama de clases

```mermaid
classDiagram
    direction LR

    class Identifiable~ID~ {
        <<interface>>
        +getId() ID
    }

    class Book {
        -String title
        -String author
        -String genre
        -boolean available
        +getId() String
        +isAvailable() boolean
        +markAsLoaned()
        +markAsReturned()
    }

    class User {
        -String name
        -String email
        -int loanLimit
        -int currentLoans
        +getId() String
        +canBorrow() boolean
        +registerLoan()
        +registerReturn()
    }

    class Loan {
        -Long id
        -Book book
        -User user
        -LocalDate startDate
        -LocalDate endDate
        -LocalDate returnDate
        -LoanStatus status
        +isActive() boolean
        +isOverdue(LocalDate today) boolean
        +markAsReturned(LocalDate date)
    }

    class LoanStatus {
        <<enumeration>>
        ACTIVE
        RETURNED
    }

    class Repository~T, ID~ {
        <<interface>>
        +save(T entity) T
        +findById(ID id) Optional~T~
        +findAll() List~T~
        +existsById(ID id) boolean
    }

    class InMemoryRepository~T, ID~ {
        <<abstract>>
        -Map~ID, T~ storage
    }

    Identifiable <|.. Book
    Identifiable <|.. User
    Identifiable <|.. Loan
    Loan "0..*" --> "1" Book : presta
    Loan "0..*" --> "1" User : pertenece a
    Loan --> LoanStatus
    Repository <|.. InMemoryRepository
    InMemoryRepository <|-- BookRepository
    InMemoryRepository <|-- UserRepository
    InMemoryRepository <|-- LoanRepository
```

## Excepciones

```mermaid
classDiagram
    RuntimeException <|-- LibraryException
    LibraryException <|-- InvalidDataException
    LibraryException <|-- DuplicateResourceException
    LibraryException <|-- ResourceNotFoundException
    LibraryException <|-- BusinessRuleException
    ResourceNotFoundException <|-- BookNotFoundException
    ResourceNotFoundException <|-- UserNotFoundException
    ResourceNotFoundException <|-- LoanNotFoundException
    BusinessRuleException <|-- BookNotAvailableException
    BusinessRuleException <|-- LoanLimitExceededException
    BusinessRuleException <|-- LoanAlreadyReturnedException
```

## Relaciones

- **Loan → Book / User: asociación.** El préstamo referencia a un libro y a un usuario que existen de forma independiente; borrar un préstamo no borra al libro ni al usuario. Un libro o un usuario pueden tener muchos préstamos en el tiempo, pero un libro solo tiene un préstamo `ACTIVE` a la vez.
- **Loan → LoanStatus: composición.** El estado no existe fuera del préstamo.
- **Identificadores:** el libro se identifica por su título, el usuario por su email (lo que pide el enunciado y usa la API) y el préstamo por un `Long` secuencial.

# Decisiones de diseño

## ¿Qué representa cada clase? (queEs)

| Clase | Representa |
|-------|-----------|
| `Book`, `User`, `Loan` | Entidades del dominio. Guardan su estado y las reglas que lo protegen. |
| `LoanStatus` | Ciclo de vida del préstamo (`ACTIVE` → `RETURNED`). |
| `Identifiable<ID>` / `Repository<T, ID>` / `InMemoryRepository<T, ID>` | Contratos genéricos de identidad y almacenamiento; una sola implementación sirve a las tres entidades. |
| `BookService`, `UserService`, `LoanService` | Casos de uso: registrar, consultar, prestar y devolver. |
| `LibraryController` | Expone los casos de uso por HTTP. |
| `GlobalExceptionHandler` | Traduce excepciones del dominio a respuestas HTTP. |

## ¿Para qué sirven los métodos? (paraQueSirve)

- Las entidades no tienen setters públicos: el estado cambia solo con métodos con intención (`markAsLoaned`, `registerLoan`, `markAsReturned`) que validan antes de modificar (**encapsulamiento**).
- Los constructores validan los datos, así no puede existir un `User` con email inválido ni un `Loan` con fechas invertidas.
- `LoanService.borrowBook` revisa **todas** las reglas antes de mutar nada, para que un préstamo rechazado no deje al libro o al usuario a medio actualizar.

## ¿Cómo se relacionan? (comoSeUsa)

`Controller → Service → Repository`. `LoanService` usa `BookService` y `UserService` para resolver las entidades (lanzan `*NotFoundException` si no existen) y luego crea el `Loan`, que asocia ambas.

## ¿Qué excepciones pueden ocurrir? (erroresComunes)

| Excepción | Cuándo | HTTP |
|-----------|--------|------|
| `InvalidDataException` | Campo vacío, email mal formado, límite ≤ 0, fechas inválidas | 400 |
| `BookNotFoundException`, `UserNotFoundException`, `LoanNotFoundException` | El recurso no existe | 404 |
| `DuplicateResourceException` | Título o email ya registrado | 409 |
| `BookNotAvailableException` | El libro ya está prestado | 409 |
| `LoanLimitExceededException` | El usuario alcanzó su límite | 409 |
| `LoanAlreadyReturnedException` | Se devuelve un préstamo ya devuelto | 409 |
| JSON inválido, parámetro faltante o de tipo incorrecto | Errores del cliente detectados por Spring | 400 |
| Cualquier otra | Error no previsto (se registra en el log) | 500 |

## Decisiones sobre las excepciones (queDecisionesImplica)

1. **Unchecked (`RuntimeException`).** Son errores de reglas de negocio que el llamador no puede "recuperar" localmente; se propagan hasta un único punto de manejo sin obligar a declarar `throws` en cada capa.
2. **Jerarquía por familias.** `ResourceNotFoundException` → 404, `BusinessRuleException` → 409. El `@RestControllerAdvice` maneja la clase padre (polimorfismo), así una excepción nueva hereda el código HTTP correcto.
3. **Excepciones específicas con mensaje claro** en vez de devolver `null` o un `400` vacío; el cliente recibe un `ErrorResponse` con `status`, `message` y `path`.
4. **Sin try-catch disperso.** El único `catch` global es el manejador; el catch-all de `Exception` evita filtrar stack traces y responde 500 genérico.
5. **Concurrencia.** `borrowBook` y `returnBook` son `synchronized` para que dos peticiones simultáneas no presten el mismo libro.

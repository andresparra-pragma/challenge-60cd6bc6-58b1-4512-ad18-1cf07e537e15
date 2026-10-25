# Pruebas manuales de la API con cURL

Guía para probar a mano los endpoints de `LibraryController` (`/library`).

## Preparación

```bash
./mvnw spring-boot:run      # levanta la app en http://localhost:8080
```

- Los datos viven en memoria, así que se borran cada vez que reinicias la app.
- Los IDs de préstamo empiezan en `1`, en el orden en que se crean.
- Los comandos se corren en el orden del documento, porque cada uno depende del estado que dejan los anteriores.
- En la URL, los espacios del título se escriben como `%20` (`Clean%20Code`).
- Si tienes `jq` instalado, cambia `-i` por `-s` y agrega `| jq` al final para ver el JSON formateado.

## Resumen de endpoints

| Método | Ruta | Descripción | Éxito |
|---|---|---|---|
| POST | `/library/books` | Crear libro | 201 |
| GET | `/library/books` | Listar libros | 200 |
| GET | `/library/books/{title}` | Buscar libro por título | 200 |
| POST | `/library/users` | Crear usuario | 201 |
| GET | `/library/users` | Listar usuarios | 200 |
| GET | `/library/users/{email}` | Buscar usuario por email | 200 |
| GET | `/library/users/{email}/loans` | Préstamos activos del usuario | 200 |
| POST | `/library/loans?userEmail=&bookTitle=` | Prestar libro | 201 |
| GET | `/library/loans` | Listar préstamos | 200 |
| GET | `/library/loans/{id}` | Buscar préstamo por id | 200 |
| PATCH | `/library/loans/{id}/return` | Devolver libro | 200 |

## 1. Flujo exitoso

### Libros

```bash
# Crear libros → 201
curl -i -X POST http://localhost:8080/library/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Clean Code","author":"Robert C. Martin","genre":"Software"}'

curl -i -X POST http://localhost:8080/library/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Dune","author":"Frank Herbert","genre":"Sci-Fi"}'

# Listar todos → 200
curl -i http://localhost:8080/library/books

# Buscar por título → 200
curl -i http://localhost:8080/library/books/Clean%20Code
```

### Usuarios

```bash
# Crear usuarios → 201
curl -i -X POST http://localhost:8080/library/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Ana Gómez","email":"ana@mail.com","loanLimit":1}'

curl -i -X POST http://localhost:8080/library/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Luis Pérez","email":"luis@mail.com","loanLimit":3}'

# Listar todos y buscar por email → 200
curl -i http://localhost:8080/library/users
curl -i http://localhost:8080/library/users/ana@mail.com
```

### Préstamos

```bash
# Ana pide "Clean Code" → 201 (préstamo id=1, vence en 2 semanas)
curl -i -X POST "http://localhost:8080/library/loans?userEmail=ana@mail.com&bookTitle=Clean%20Code"

# Préstamos activos de Ana → 200
curl -i http://localhost:8080/library/users/ana@mail.com/loans

# Listar todos y buscar por id → 200
curl -i http://localhost:8080/library/loans
curl -i http://localhost:8080/library/loans/1

# Devolver el préstamo → 200 (status RETURNED)
curl -i -X PATCH http://localhost:8080/library/loans/1/return
```

## 2. Casos de error

Todas las respuestas de error traen un cuerpo `ErrorResponse`:

```json
{
  "timestamp": "2026-10-25T10:15:30",
  "status": 409,
  "error": "Conflict",
  "message": "...",
  "path": "/library/books"
}
```

### 400: datos inválidos

```bash
# Título en blanco
curl -i -X POST http://localhost:8080/library/books \
  -H "Content-Type: application/json" \
  -d '{"title":"  ","author":"X","genre":"Y"}'

# Email con formato inválido
curl -i -X POST http://localhost:8080/library/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Bad","email":"no-es-email","loanLimit":2}'

# loanLimit omitido o en 0 (debe ser mayor que 0)
curl -i -X POST http://localhost:8080/library/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Sin limite","email":"x@mail.com"}'

# JSON mal formado
curl -i -X POST http://localhost:8080/library/books \
  -H "Content-Type: application/json" -d '{"title":'

# Falta el parámetro bookTitle
curl -i -X POST "http://localhost:8080/library/loans?userEmail=ana@mail.com"

# El id del préstamo no es numérico
curl -i http://localhost:8080/library/loans/abc
```

### 404: recurso inexistente

```bash
curl -i http://localhost:8080/library/books/No%20Existe
curl -i http://localhost:8080/library/users/nadie@mail.com
curl -i http://localhost:8080/library/loans/999
curl -i -X POST "http://localhost:8080/library/loans?userEmail=nadie@mail.com&bookTitle=Dune"
```

### 409: reglas de negocio y duplicados

```bash
# Libro duplicado (el título es el identificador)
curl -i -X POST http://localhost:8080/library/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Dune","author":"Otro","genre":"Otro"}'

# Usuario duplicado (el email es el identificador)
curl -i -X POST http://localhost:8080/library/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Otra Ana","email":"ana@mail.com","loanLimit":2}'

# Devolver un préstamo que ya fue devuelto
curl -i -X PATCH http://localhost:8080/library/loans/1/return

# Libro ya prestado: Luis pide Dune (201) y después Ana intenta pedirlo (409)
curl -i -X POST "http://localhost:8080/library/loans?userEmail=luis@mail.com&bookTitle=Dune"
curl -i -X POST "http://localhost:8080/library/loans?userEmail=ana@mail.com&bookTitle=Dune"

# Límite de préstamos superado: Ana (límite 1) toma Clean Code (201)
# y luego intenta pedir Refactoring (409)
curl -i -X POST "http://localhost:8080/library/loans?userEmail=ana@mail.com&bookTitle=Clean%20Code"
curl -i -X POST http://localhost:8080/library/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Refactoring","author":"Martin Fowler","genre":"Software"}'
curl -i -X POST "http://localhost:8080/library/loans?userEmail=ana@mail.com&bookTitle=Refactoring"
```

## 3. Verificar que un rechazo no deja estado parcial

Después del último préstamo rechazado, el libro debe seguir disponible y el contador de Ana no debe cambiar. El servicio valida todas las reglas antes de modificar el libro o el usuario.

```bash
curl -i http://localhost:8080/library/books/Refactoring          # sigue disponible
curl -i http://localhost:8080/library/users/ana@mail.com/loans   # solo Clean Code
```

package com.library.exception;

/** Los datos recibidos no cumplen las reglas de una entidad. */
public class InvalidDataException extends LibraryException {
    public InvalidDataException(String message) {
        super(message);
    }
}

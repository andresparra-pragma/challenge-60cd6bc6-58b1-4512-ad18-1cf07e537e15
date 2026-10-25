package com.library.exception;

/**
 * Raíz de las excepciones del dominio de la biblioteca.
 * Es unchecked para que se propague hasta el manejador global sin ensuciar las firmas.
 */
public abstract class LibraryException extends RuntimeException {
    protected LibraryException(String message) {
        super(message);
    }
}

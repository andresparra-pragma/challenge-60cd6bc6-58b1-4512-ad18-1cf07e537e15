package com.library.exception;

/** El recurso solicitado no existe. */
public abstract class ResourceNotFoundException extends LibraryException {
    protected ResourceNotFoundException(String message) {
        super(message);
    }
}

package com.library.exception;

public class BookNotAvailableException extends BusinessRuleException {
    public BookNotAvailableException(String title) {
        super("El libro '" + title + "' no está disponible");
    }
}

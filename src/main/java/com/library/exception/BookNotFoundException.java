package com.library.exception;

public class BookNotFoundException extends ResourceNotFoundException {
    public BookNotFoundException(String title) {
        super("No existe un libro con el título '" + title + "'");
    }
}

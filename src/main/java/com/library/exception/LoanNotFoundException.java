package com.library.exception;

public class LoanNotFoundException extends ResourceNotFoundException {
    public LoanNotFoundException(Long id) {
        super("No existe un préstamo con id " + id);
    }
}

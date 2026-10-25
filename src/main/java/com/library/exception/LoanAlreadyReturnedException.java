package com.library.exception;

public class LoanAlreadyReturnedException extends BusinessRuleException {
    public LoanAlreadyReturnedException(Long id) {
        super("El préstamo " + id + " ya fue devuelto");
    }
}

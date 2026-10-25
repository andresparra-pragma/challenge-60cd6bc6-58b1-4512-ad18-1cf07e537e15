package com.library.exception;

/** La operación es válida sintácticamente pero viola una regla del negocio. */
public abstract class BusinessRuleException extends LibraryException {
    protected BusinessRuleException(String message) {
        super(message);
    }
}

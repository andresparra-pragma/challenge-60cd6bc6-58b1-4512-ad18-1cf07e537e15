package com.library.exception;

public class LoanLimitExceededException extends BusinessRuleException {
    public LoanLimitExceededException(String email, int loanLimit) {
        super("El usuario '" + email + "' alcanzó su límite de " + loanLimit + " préstamos");
    }
}

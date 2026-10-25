package com.library.model;

import com.library.exception.InvalidDataException;
import com.library.exception.LoanLimitExceededException;

public class User implements Identifiable<String> {
    private final String name;
    private final String email;
    private final int loanLimit;
    private int currentLoans;

    public User(String name, String email, int loanLimit) {
        this.name = Validations.requireNotBlank(name, "name");
        this.email = Validations.requireNotBlank(email, "email");
        if (!this.email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new InvalidDataException("El email '" + email + "' no tiene un formato válido");
        }
        if (loanLimit <= 0) {
            throw new InvalidDataException("El límite de préstamos debe ser mayor que cero");
        }
        this.loanLimit = loanLimit;
        this.currentLoans = 0;
    }

    /** El email identifica al usuario dentro de la biblioteca. */
    @Override
    public String getId() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getLoanLimit() {
        return loanLimit;
    }

    public int getCurrentLoans() {
        return currentLoans;
    }

    public boolean canBorrow() {
        return currentLoans < loanLimit;
    }

    public void registerLoan() {
        if (!canBorrow()) {
            throw new LoanLimitExceededException(email, loanLimit);
        }
        currentLoans++;
    }

    public void registerReturn() {
        if (currentLoans > 0) {
            currentLoans--;
        }
    }
}

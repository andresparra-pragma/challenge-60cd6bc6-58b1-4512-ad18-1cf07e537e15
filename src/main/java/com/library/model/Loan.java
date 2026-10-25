package com.library.model;

import com.library.exception.InvalidDataException;
import com.library.exception.LoanAlreadyReturnedException;

import java.time.LocalDate;

public class Loan implements Identifiable<Long> {
    private final Long id;
    private final Book book;
    private final User user;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private LocalDate returnDate;
    private LoanStatus status;

    public Loan(Long id, Book book, User user, LocalDate startDate, LocalDate endDate) {
        this.id = Validations.requireNotNull(id, "id");
        this.book = Validations.requireNotNull(book, "book");
        this.user = Validations.requireNotNull(user, "user");
        this.startDate = Validations.requireNotNull(startDate, "startDate");
        this.endDate = Validations.requireNotNull(endDate, "endDate");
        if (!endDate.isAfter(startDate)) {
            throw new InvalidDataException("La fecha de devolución debe ser posterior a la fecha de inicio");
        }
        this.status = LoanStatus.ACTIVE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public boolean isActive() {
        return status == LoanStatus.ACTIVE;
    }

    public boolean isOverdue(LocalDate today) {
        return isActive() && today.isAfter(endDate);
    }

    public void markAsReturned(LocalDate date) {
        if (!isActive()) {
            throw new LoanAlreadyReturnedException(id);
        }
        this.returnDate = Validations.requireNotNull(date, "returnDate");
        this.status = LoanStatus.RETURNED;
    }
}

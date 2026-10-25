package com.library.model;

import com.library.exception.InvalidDataException;
import com.library.exception.LoanAlreadyReturnedException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanTest {
    private final Book book = new Book("Dune", "Frank Herbert", "Ciencia ficción");
    private final User user = new User("Ana", "ana@mail.com", 2);
    private final LocalDate start = LocalDate.of(2026, 1, 1);

    @Test
    void endDateMustBeAfterStartDate() {
        assertThrows(InvalidDataException.class, () -> new Loan(1L, book, user, start, start));
    }

    @Test
    void isOverdueOnlyWhileActiveAndPastEndDate() {
        Loan loan = new Loan(1L, book, user, start, start.plusDays(14));

        assertFalse(loan.isOverdue(start.plusDays(14)));
        assertTrue(loan.isOverdue(start.plusDays(15)));

        loan.markAsReturned(start.plusDays(20));
        assertFalse(loan.isOverdue(start.plusDays(30)));
    }

    @Test
    void cannotBeReturnedTwice() {
        Loan loan = new Loan(1L, book, user, start, start.plusDays(14));
        loan.markAsReturned(start.plusDays(3));

        assertEquals(LoanStatus.RETURNED, loan.getStatus());
        assertThrows(LoanAlreadyReturnedException.class, () -> loan.markAsReturned(start.plusDays(4)));
    }
}

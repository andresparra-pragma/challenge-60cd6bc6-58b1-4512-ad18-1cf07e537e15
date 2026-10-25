package com.library.service;

import com.library.exception.BookNotAvailableException;
import com.library.exception.BookNotFoundException;
import com.library.exception.LoanLimitExceededException;
import com.library.exception.UserNotFoundException;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 3, 10);

    private BookService bookService;
    private UserService userService;
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-03-10T12:00:00Z"), ZoneOffset.UTC);
        bookService = new BookService(new BookRepository());
        userService = new UserService(new UserRepository());
        loanService = new LoanService(new LoanRepository(), bookService, userService, clock);

        bookService.addBook(new Book("Dune", "Frank Herbert", "Ciencia ficción"));
        bookService.addBook(new Book("Emma", "Jane Austen", "Novela"));
        userService.addUser(new User("Ana", "ana@mail.com", 1));
    }

    @Test
    void borrowBookUpdatesBookAndUser() {
        Loan loan = loanService.borrowBook("ana@mail.com", "Dune");

        assertEquals(TODAY, loan.getStartDate());
        assertEquals(TODAY.plusWeeks(2), loan.getEndDate());
        assertFalse(bookService.isBookAvailable("Dune"));
        assertEquals(1, userService.getUserByEmail("ana@mail.com").getCurrentLoans());
    }

    @Test
    void cannotBorrowUnavailableBook() {
        userService.addUser(new User("Luis", "luis@mail.com", 3));
        loanService.borrowBook("ana@mail.com", "Dune");

        assertThrows(BookNotAvailableException.class, () -> loanService.borrowBook("luis@mail.com", "Dune"));
    }

    @Test
    void failedLoanDoesNotChangeState() {
        loanService.borrowBook("ana@mail.com", "Dune");

        assertThrows(LoanLimitExceededException.class, () -> loanService.borrowBook("ana@mail.com", "Emma"));
        assertTrue(bookService.isBookAvailable("Emma"));
        assertEquals(1, loanService.getAllLoans().size());
    }

    @Test
    void unknownUserOrBookThrowsNotFound() {
        assertThrows(UserNotFoundException.class, () -> loanService.borrowBook("nadie@mail.com", "Dune"));
        assertThrows(BookNotFoundException.class, () -> loanService.borrowBook("ana@mail.com", "Nada"));
    }

    @Test
    void returnBookReleasesBookAndUserQuota() {
        Loan loan = loanService.borrowBook("ana@mail.com", "Dune");

        loanService.returnBook(loan.getId());

        assertFalse(loan.isActive());
        assertEquals(TODAY, loan.getReturnDate());
        assertTrue(bookService.isBookAvailable("Dune"));
        assertTrue(userService.isUserUnderLoanLimit("ana@mail.com"));
        assertTrue(loanService.getActiveLoansByUser("ana@mail.com").isEmpty());
    }
}

package com.library.service;

import com.library.exception.BookNotAvailableException;
import com.library.exception.LoanLimitExceededException;
import com.library.exception.LoanNotFoundException;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.User;
import com.library.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class LoanService {
    static final Period LOAN_PERIOD = Period.ofWeeks(2);

    private final LoanRepository loanRepository;
    private final BookService bookService;
    private final UserService userService;
    private final Clock clock;
    private final AtomicLong sequence = new AtomicLong();

    public LoanService(LoanRepository loanRepository, BookService bookService, UserService userService, Clock clock) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
        this.userService = userService;
        this.clock = clock;
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(id));
    }

    public List<Loan> getActiveLoansByUser(String email) {
        User user = userService.getUserByEmail(email);
        return loanRepository.findAll().stream()
                .filter(loan -> loan.isActive() && loan.getUser().equals(user))
                .toList();
    }

    public boolean canUserBorrowBook(User user, Book book) {
        return user.canBorrow() && book.isAvailable();
    }

    /**
     * Registra un préstamo. Se validan ambas reglas antes de modificar cualquier estado
     * para no dejar al usuario o al libro a medio actualizar.
     */
    public synchronized Loan borrowBook(String userEmail, String bookTitle) {
        User user = userService.getUserByEmail(userEmail);
        Book book = bookService.getBookByTitle(bookTitle);
        if (!book.isAvailable()) {
            throw new BookNotAvailableException(book.getTitle());
        }
        if (!user.canBorrow()) {
            throw new LoanLimitExceededException(user.getEmail(), user.getLoanLimit());
        }

        LocalDate today = LocalDate.now(clock);
        Loan loan = new Loan(sequence.incrementAndGet(), book, user, today, today.plus(LOAN_PERIOD));
        book.markAsLoaned();
        user.registerLoan();
        return loanRepository.save(loan);
    }

    public synchronized Loan returnBook(Long loanId) {
        Loan loan = getLoanById(loanId);
        loan.markAsReturned(LocalDate.now(clock));
        loan.getBook().markAsReturned();
        loan.getUser().registerReturn();
        return loan;
    }
}

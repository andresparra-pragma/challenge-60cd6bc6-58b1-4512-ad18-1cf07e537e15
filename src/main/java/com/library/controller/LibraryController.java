package com.library.controller;

import com.library.controller.dto.BookRequest;
import com.library.controller.dto.UserRequest;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.User;
import com.library.service.BookService;
import com.library.service.LoanService;
import com.library.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/library")
public class LibraryController {
    private final BookService bookService;
    private final UserService userService;
    private final LoanService loanService;

    public LibraryController(BookService bookService, UserService userService, LoanService loanService) {
        this.bookService = bookService;
        this.userService = userService;
        this.loanService = loanService;
    }

    @PostMapping("/books")
    public ResponseEntity<Book> addBook(@RequestBody BookRequest request) {
        return new ResponseEntity<>(bookService.addBook(request.toBook()), HttpStatus.CREATED);
    }

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/books/{title}")
    public Book getBook(@PathVariable String title) {
        return bookService.getBookByTitle(title);
    }

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody UserRequest request) {
        return new ResponseEntity<>(userService.addUser(request.toUser()), HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{email}")
    public User getUser(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("/users/{email}/loans")
    public List<Loan> getActiveLoans(@PathVariable String email) {
        return loanService.getActiveLoansByUser(email);
    }

    @PostMapping("/loans")
    public ResponseEntity<Loan> borrowBook(@RequestParam String userEmail, @RequestParam String bookTitle) {
        return new ResponseEntity<>(loanService.borrowBook(userEmail, bookTitle), HttpStatus.CREATED);
    }

    @GetMapping("/loans")
    public List<Loan> getAllLoans() {
        return loanService.getAllLoans();
    }

    @GetMapping("/loans/{id}")
    public Loan getLoan(@PathVariable Long id) {
        return loanService.getLoanById(id);
    }

    @PatchMapping("/loans/{id}/return")
    public Loan returnBook(@PathVariable Long id) {
        return loanService.returnBook(id);
    }
}

package com.library.service;

import com.library.exception.BookNotFoundException;
import com.library.exception.DuplicateResourceException;
import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book addBook(Book book) {
        if (bookRepository.existsById(book.getId())) {
            throw new DuplicateResourceException("libro", book.getId());
        }
        return bookRepository.save(book);
    }

    public Book getBookByTitle(String title) {
        return bookRepository.findById(title)
                .orElseThrow(() -> new BookNotFoundException(title));
    }

    public boolean isBookAvailable(String title) {
        return getBookByTitle(title).isAvailable();
    }
}

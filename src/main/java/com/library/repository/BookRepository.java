package com.library.repository;

import com.library.model.Book;

@org.springframework.stereotype.Repository
public class BookRepository extends InMemoryRepository<Book, String> {
}

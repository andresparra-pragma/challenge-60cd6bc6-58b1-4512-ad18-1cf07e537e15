package com.library.controller.dto;

import com.library.model.Book;

public record BookRequest(String title, String author, String genre) {
    public Book toBook() {
        return new Book(title, author, genre);
    }
}

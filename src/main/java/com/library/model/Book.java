package com.library.model;

import com.library.exception.BookNotAvailableException;

public class Book implements Identifiable<String> {
    private final String title;
    private final String author;
    private final String genre;
    private boolean available;

    public Book(String title, String author, String genre) {
        this.title = Validations.requireNotBlank(title, "title");
        this.author = Validations.requireNotBlank(author, "author");
        this.genre = Validations.requireNotBlank(genre, "genre");
        this.available = true;
    }

    /** El título identifica al libro dentro de la biblioteca. */
    @Override
    public String getId() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public boolean isAvailable() {
        return available;
    }

    public void markAsLoaned() {
        if (!available) {
            throw new BookNotAvailableException(title);
        }
        available = false;
    }

    public void markAsReturned() {
        available = true;
    }
}

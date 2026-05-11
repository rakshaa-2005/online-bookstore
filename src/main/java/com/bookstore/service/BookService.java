package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepo;

    public BookService(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    public List<Book> searchBooks(String query) {
        if (query == null || query.isBlank()) return getAllBooks();
        return bookRepo.search(query.trim());
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepo.findById(id);
    }
}

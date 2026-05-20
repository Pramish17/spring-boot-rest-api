package com.example.CRUD.service;

import com.example.CRUD.model.Book;
import com.example.CRUD.repository.BookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookTransactionService {

    @Autowired
    private BookRepo bookRepo;

    @Transactional
    public Book saveBookTransactional(Book book) {
        Book savedBook = bookRepo.save(book);
        throw new RuntimeException("Test: should this rollback?");
    }
}
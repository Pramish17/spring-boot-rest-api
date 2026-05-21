package com.example.CRUD.service;

import com.example.CRUD.exception.ResourceNotFoundException;
import com.example.CRUD.model.Book;
import com.example.CRUD.repository.BookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private BookTransactionService bookTransactionService;

    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    @Transactional
    public Book addBook(Book book) {
        return bookRepo.save(book);
    }

    @Transactional
    public Optional<Book> updateBook(Long id, Book newBookData) {
        Optional<Book> existingBook = bookRepo.findById(id);
        if (existingBook.isPresent()) {
            Book bookToUpdate = existingBook.get();
            bookToUpdate.setTitle(newBookData.getTitle());
            bookToUpdate.setAuthor(newBookData.getAuthor());
            return Optional.of(bookRepo.save(bookToUpdate));
        }
        return Optional.empty();
    }

    @Transactional
    public boolean deleteBookById(Long id) {
        if (bookRepo.existsById(id)) {
            bookRepo.deleteById(id);
            return true;
        }
        return false;
    }


    public Book addBookWithInternalCall(Book book) {
        // This method calls another @Transactional method in the SAME class
        return saveBookTransactional(book);
    }

    @Transactional
    public Book saveBookTransactional(Book book) {
        Book savedBook = bookRepo.save(book);
        // Simulate a failure AFTER save
        throw new RuntimeException("Test: should this rollback?");
    }

    public Book addBookWithExternalCall(Book book) {
        // Now calls a method on a DIFFERENT bean (with proxy in between)
        return bookTransactionService.saveBookTransactional(book);
    }

    public List<Book> findBooksByAuthor (String author){
        return bookRepo.findAll().stream().filter(book-> book.getAuthor().equalsIgnoreCase(author)).collect(Collectors.toList());
    }
    public List<String> getAllBookTitlesSorted() {
        return bookRepo.findAll().stream()
                .map(Book::getTitle)
                .sorted()
                .collect(Collectors.toList());
    }

    public Map<String, Long> countBooksGroupedByAuthor() {
        return bookRepo.findAll().stream().collect(Collectors.groupingBy(Book::getAuthor, Collectors.counting()));
    }
}
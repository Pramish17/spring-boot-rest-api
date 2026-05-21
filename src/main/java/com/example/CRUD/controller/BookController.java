package com.example.CRUD.controller;

import com.example.CRUD.model.Book;
import com.example.CRUD.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        Book savedBook = bookService.addBook(book);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBookById(@PathVariable Long id, @Valid @RequestBody Book newBookData) {
        Optional<Book> updatedBook = bookService.updateBook(id, newBookData);
        return updatedBook.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookById(@PathVariable Long id) {
        boolean deleted = bookService.deleteBookById(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/test-gotcha")
    public ResponseEntity<String> testGotcha(@RequestBody Book book) {
        try {
            bookService.addBookWithInternalCall(book);
            return new ResponseEntity<>("Saved (no exception)", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Exception thrown: " + e.getMessage(), HttpStatus.OK);
        }
    }

    @PostMapping("/test-fix")
    public ResponseEntity<String> testFix(@RequestBody Book book) {
        try {
            bookService.addBookWithExternalCall(book);
            return new ResponseEntity<>("Saved (no exception)", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Exception thrown: " + e.getMessage(), HttpStatus.OK);
        }
    }

    @GetMapping("/by-author/{author}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable String author) {
        return new ResponseEntity<>(bookService.findBooksByAuthor(author), HttpStatus.OK);
    }

    @GetMapping("/titles")
    public ResponseEntity<List<String>> getAllTitles() {
        return new ResponseEntity<>(bookService.getAllBookTitlesSorted(), HttpStatus.OK);
    }

    @GetMapping("/grouped-by-author")
    public ResponseEntity<Map<String, Long>> getGroupedByAuthor() {
        return new ResponseEntity<>(bookService.countBooksGroupedByAuthor(), HttpStatus.OK);
    }
}
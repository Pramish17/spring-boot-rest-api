package com.example.CRUD.service;

import com.example.CRUD.exception.ResourceNotFoundException;
import com.example.CRUD.model.Book;
import com.example.CRUD.repository.BookRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepo bookRepo;

    @InjectMocks
    private BookService bookService;

    @Test
    void getBookById_whenBookExists_returnsBook() {
        Book book = new Book(1L, "Test Title", "Test Author");
        when(bookRepo.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        assertEquals("Test Title", result.getTitle());
        verify(bookRepo).findById(1L);
    }

    @Test
    void getBookById_whenBookMissing_throwsResourceNotFoundException() {
        when(bookRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> bookService.getBookById(999L));
    }

    @Test
    void getAllBooksTest(){
        Book book1 = new Book(1L, "Test Title1", "Test Author1");
        Book book2 = new Book(2L, "Test Title2", "Test Author2");

        when(bookRepo.findAll()).thenReturn(List.of(book1,book2));

        List<Book> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        assertEquals("Test Author2", book2.getAuthor());
    }

    @Test
    void deleteBookByIdTest(){
        Long bookId = 1L;
        when(bookRepo.existsById(bookId)).thenReturn(true);

        boolean result = bookService.deleteBookById(bookId);

        assertTrue(result);
        verify(bookRepo).existsById(bookId);
        verify(bookRepo).deleteById(bookId);

    }
    @Test
    void deleteBookByIdDoesNotExistTest(){
        Long bookId = 99L;
        when(bookRepo.existsById(bookId)).thenReturn(false);

        boolean result = bookService.deleteBookById(bookId);

        assertFalse(result);
        verify(bookRepo).existsById(bookId);
        verify(bookRepo, never()).deleteById(bookId);

    }

    @Test
    void addBook_savesAndReturnsBook() {
        Book bookToSave = new Book(null, "Test Title", "Test Author");
        Book savedBook = new Book(1L, "Test Title", "Test Author");
        when(bookRepo.save(bookToSave)).thenReturn(savedBook);

        Book result = bookService.addBook(bookToSave);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        verify(bookRepo).save(bookToSave);
    }

    @Test
    void testFindBooksByAuthor() {

        // Arrange
        Book book1 = new Book(1L, "Java Basics", "John");
        Book book2 = new Book(2L, "Spring Boot", "JOHN");
        Book book3 = new Book(3L, "Python", "David");

        when(bookRepo.findAll())
                .thenReturn(Arrays.asList(book1, book2, book3));

        List<Book> result = bookService.findBooksByAuthor("john");

        // Assert
        assertEquals(2, result.size());
        assertEquals("Java Basics", result.get(0).getTitle());
        assertEquals("Spring Boot", result.get(1).getTitle());
    }
}

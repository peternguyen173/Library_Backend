package com.example.Backend.service;

import com.example.Backend.entity.Book;
import com.example.Backend.exception.BadRequestException;
import com.example.Backend.exception.NotFoundException;
import com.example.Backend.repository.BookRepository;
import com.example.Backend.request.BookRequest;
import com.example.Backend.response.BookResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllBooks() {
        List<BookResponse> bookResponses = new ArrayList<>();
        Page<BookResponse> page = new PageImpl<>(bookResponses);
        when(bookRepository.findAllBooks(any(Pageable.class))).thenReturn(page);

        Page<BookResponse> result = bookService.getAllBooks(0, 10);

        assertNotNull(result);
        verify(bookRepository, times(1)).findAllBooks(any(Pageable.class));
    }

    @Test
    void testGetBookById_Found() throws NotFoundException {
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.getBookById(1L));
        verify(bookRepository, times(1)).findById(anyLong());
    }

    @Test
    void testAddBook_Success() throws BadRequestException {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("New Book");
        bookRequest.setAuthor("Author");

        when(bookRepository.existsByTitleAndAuthor(anyString(), anyString())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(new Book());

        Book result = bookService.addBook(bookRequest);

        assertNotNull(result);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testAddBook_BadRequestException() {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Existing Book");
        bookRequest.setAuthor("Author");

        when(bookRepository.existsByTitleAndAuthor(anyString(), anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> bookService.addBook(bookRequest));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testEditBook_Success() throws NotFoundException, BadRequestException {
        Book existingBook = new Book();
        existingBook.setId(1L);
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Updated Title");

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

        Book result = bookService.editBook(1L, bookRequest);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testEditBook_NotFoundException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
        BookRequest bookRequest = new BookRequest();

        assertThrows(NotFoundException.class, () -> bookService.editBook(1L, bookRequest));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testDeleteBook_Success() throws NotFoundException {
        Book book = new Book();
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).delete(any(Book.class));
    }

    @Test
    void testDeleteBook_NotFoundException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.deleteBook(1L));
        verify(bookRepository, never()).delete(any(Book.class));
    }
}

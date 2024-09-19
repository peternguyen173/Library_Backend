package com.example.Backend.service;

import com.example.Backend.entity.Book;
import com.example.Backend.exception.BadRequestException;
import com.example.Backend.exception.NotFoundException;
import com.example.Backend.repository.BookProjection;
import com.example.Backend.repository.BookRepository;
import com.example.Backend.request.BookRequest;
import com.example.Backend.response.BookDTO;
import com.example.Backend.response.BookResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public Page<BookResponse> getAllBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAllBooks(pageable);
    }

    public Book getBookById(Long id) throws NotFoundException {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book with ID " + id + " is not found!"));
    }


    public  Page<BookResponse>  searchBookByTitle(String title,Pageable pageable)  {
        Page<BookResponse>  productList = bookRepository.searchBookByTitle(title, pageable);
        return productList;
    }

    public Page<BookResponse> searchBookByAuthor(String author,Pageable pageable)  {
        Page<BookResponse> productList = bookRepository.searchBookByAuthor(author, pageable);
        return productList;
    }

    public Page<BookResponse> searchBookByCategory(String category,Pageable pageable)    {
        Page<BookResponse> productList = bookRepository.searchBookByCategory(category,pageable);
        return productList;
    }


    @Transactional
    public Book addBook(BookRequest bookRequest) throws BadRequestException {
        if(bookRepository.existsByTitleAndAuthor(bookRequest.getTitle(),bookRequest.getAuthor()))
            throw new BadRequestException("Cuốn sách này của tác giả này đã có trên hệ thống!");
        try {
            Book book = new Book();
            book.setTitle(bookRequest.getTitle());
            book.setAuthor(bookRequest.getAuthor());
            book.setCategory(bookRequest.getCategory());
            book.setPublisher(bookRequest.getPublisher());
            book.setDescription(bookRequest.getDescription());
            book.setNumberOfPages(bookRequest.getNumberOfPages());
            book.setPreviewText(bookRequest.getPreviewText());
            book.setImageUrl(bookRequest.getImageUrl()); // Lưu dữ liệu ảnh
            return bookRepository.save(book); // Persist the book entity to the database
        }
        catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Transactional
    public Book editBook(Long id, BookRequest bookRequest) throws NotFoundException,BadRequestException {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book with ID " + id + " is not found!"));
        try {
        existingBook.setTitle(bookRequest.getTitle());
        existingBook.setAuthor(bookRequest.getAuthor());
        existingBook.setCategory(bookRequest.getCategory());
        existingBook.setPublisher(bookRequest.getPublisher());
        existingBook.setDescription(bookRequest.getDescription());
        existingBook.setNumberOfPages(bookRequest.getNumberOfPages());
        existingBook.setPreviewText(bookRequest.getPreviewText());
        existingBook.setImageUrl(bookRequest.getImageUrl()); // Lưu dữ liệu ảnh


        return bookRepository.save(existingBook);}
        catch (Exception e) {
                throw new BadRequestException(e.getMessage());
            }
    }

    public void deleteBook(Long id) throws NotFoundException {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book with ID " + id + " is not found!"));

        bookRepository.delete(book);
    }



}

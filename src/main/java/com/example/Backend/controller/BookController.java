package com.example.Backend.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.Backend.entity.Book;
import com.example.Backend.exception.BadRequestException;
import com.example.Backend.exception.NotFoundException;
import com.example.Backend.repository.BookProjection;
import com.example.Backend.request.BookRequest;
import com.example.Backend.response.BookDTO;
import com.example.Backend.response.BookResponse;
import com.example.Backend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping
    public ResponseEntity<Page<BookResponse>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BookResponse> books = bookService.getAllBooks(page, size);

        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        try {
            Book book = bookService.getBookById(id);
            return new ResponseEntity<>(book, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    // Search by title with pagination
    @GetMapping("/search/title")
    public ResponseEntity<Page<BookResponse>> searchBookByTitle(
            @RequestParam String title,
            Pageable pageable) {
        Page<BookResponse> books = bookService.searchBookByTitle(title, pageable);
        return ResponseEntity.ok(books);
    }

    // Search by author with pagination
    @GetMapping("/search/author")
    public ResponseEntity<Page<BookResponse>> searchBookByAuthor(
            @RequestParam String author,
            Pageable pageable) {
        Page<BookResponse> books = bookService.searchBookByAuthor(author, pageable);
        return ResponseEntity.ok(books);
    }

    // Search by category without pagination
    @GetMapping("/search/category")
    public ResponseEntity<Page<BookResponse>> searchBookByCategory(
            @RequestParam String category,
            Pageable pageable) {
        Page<BookResponse> books = bookService.searchBookByCategory(category, pageable);
        return ResponseEntity.ok(books);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addBook(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("category") String category,
            @RequestParam("publisher") String publisher,
            @RequestParam("description") String description,
            @RequestParam("numberOfPages") int numberOfPages,
            @RequestParam("previewText") String previewText,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        try {
            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                imageUrl = (String) uploadResult.get("secure_url");
            }

            BookRequest bookRequest = new BookRequest();
            bookRequest.setTitle(title);
            bookRequest.setAuthor(author);
            bookRequest.setPublisher(publisher);
            bookRequest.setCategory(category);
            bookRequest.setDescription(description);
            bookRequest.setNumberOfPages(numberOfPages);
            bookRequest.setPreviewText(previewText);
            bookRequest.setImageUrl(imageUrl); // Set image URL in BookRequest

            Book newBook = bookService.addBook(bookRequest);
            return new ResponseEntity<>(newBook, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> editBook(@PathVariable Long id,
                                      @RequestParam("title") String title,
                                      @RequestParam("author") String author,
                                      @RequestParam("publisher") String publisher,
                                      @RequestParam("category") String category,
                                      @RequestParam("description") String description,
                                      @RequestParam("numberOfPages") int numberOfPages,
                                      @RequestParam("previewText") String previewText,
                                      @RequestParam("imageUrl") String imageUrl,
                                      @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            if (image != null && !image.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                imageUrl = (String) uploadResult.get("secure_url");
            }

            BookRequest bookRequest = new BookRequest();
            bookRequest.setTitle(title);
            bookRequest.setAuthor(author);
            bookRequest.setCategory(category);
            bookRequest.setPublisher(publisher);
            bookRequest.setDescription(description);
            bookRequest.setNumberOfPages(numberOfPages);
            bookRequest.setPreviewText(previewText);
            bookRequest.setImageUrl(imageUrl); // Set image URL in BookRequest

            Book updatedBook = bookService.editBook(id, bookRequest);
            return new ResponseEntity<>(updatedBook, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        try {
            // Xóa sách
            bookService.deleteBook(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

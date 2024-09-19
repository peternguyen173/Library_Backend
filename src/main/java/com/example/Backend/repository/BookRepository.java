package com.example.Backend.repository;

import com.example.Backend.entity.Book;
import com.example.Backend.response.BookDTO;
import com.example.Backend.response.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(
            "SELECT new com.example.Backend.response.BookResponse(b.id, b.title, b.author, b.category, b.publisher, b.description, b.numberOfPages, b.previewText, b.imageUrl) " +
                    "FROM Book b"
    )
    Page<BookResponse> findAllBooks(Pageable pageable);

    @Query(
            "SELECT new com.example.Backend.response.BookResponse(b.id, b.title, b.author, b.category, b.publisher, b.description, b.numberOfPages, b.previewText, b.imageUrl) " +
                    "FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))"
    )
    Page<BookResponse> searchBookByTitle(@Param("title") String title, Pageable pageable);

    @Query(
            "SELECT new com.example.Backend.response.BookResponse(b.id, b.title, b.author, b.category, b.publisher, b.description, b.numberOfPages, b.previewText, b.imageUrl) " +
                    "FROM Book b WHERE LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))"
    )
    Page<BookResponse> searchBookByAuthor(@Param("author") String author, Pageable pageable);

    @Query(
            "SELECT new com.example.Backend.response.BookResponse(b.id, b.title, b.author, b.category, b.publisher, b.description, b.numberOfPages, b.previewText, b.imageUrl) " +
                    "FROM Book b WHERE LOWER(b.category) LIKE LOWER(CONCAT('%', :category, '%'))"
    )
    Page<BookResponse> searchBookByCategory(@Param("category") String category, Pageable pageable);

    boolean existsByTitleAndAuthor(String title, String author);
}

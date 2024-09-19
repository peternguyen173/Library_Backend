package com.example.Backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Repository;

@Data
@Entity
@Table(name = "book", indexes = {
        @Index(name = "idx_title", columnList = "title"),
        @Index(name = "idx_author", columnList = "author")
})
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String author;

    private String category;

    private String publisher;

    private String description;

    private int numberOfPages;

    private String previewText;

    private String imageUrl;
}

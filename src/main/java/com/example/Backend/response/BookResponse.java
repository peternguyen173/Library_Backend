package com.example.Backend.response;

import com.example.Backend.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookResponse {

    private long id;

    private String title;

    private String author;

    private String category;

    private String publisher;

    private String description;

    private int numberOfPages;

    private String previewText;

    private String imageUrl;

    public BookResponse(Book book){
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.category = book.getCategory();
        this.publisher = book.getPublisher();
        this.description = book.getDescription();
        this.numberOfPages = book.getNumberOfPages();
        this.previewText = book.getPreviewText();
        this.imageUrl = book.getImageUrl();
    }

}

package com.example.Backend.repository;

public interface BookProjection {
    Long getId();
    String getTitle();
    String getAuthor();
    String getPublisher();
    String getIsbn();
    // Exclude image field
}

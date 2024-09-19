package com.example.Backend.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BookRequest {

    @NotBlank
    @NotNull
    private String title;

    @NotBlank
    @NotNull
    private String author;

    private String publisher;

    private String category;

    private String description;

    private int numberOfPages;

    private String previewText;

    private String imageUrl; // Thêm trường cho ảnh bìa

}

package com.example.Backend.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}

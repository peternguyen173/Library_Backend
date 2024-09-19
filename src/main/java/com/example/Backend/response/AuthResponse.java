package com.example.Backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String jwt;
    private String email;
    public  String name;
    private boolean status;

    public AuthResponse(String jwt , boolean status) {
        this.status = status;
        this.jwt = jwt;
    }


}

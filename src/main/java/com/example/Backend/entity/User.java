package com.example.Backend.entity;

import com.example.Backend.entity.enumModel.ERole;
import com.example.Backend.entity.enumModel.UserStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.security.Timestamp;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String email;

    private String password;

    private String name;

    private String address;

    private String phoneNumber;

    private UserStatus status;

    private String verificationToken;

    private String role;

}

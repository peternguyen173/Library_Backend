package com.example.Backend.controller;

import com.example.Backend.config.secutiry.JwtTokenProvider;
import com.example.Backend.exception.BadRequestException;
import com.example.Backend.request.AuthRequest;
import com.example.Backend.request.UserRequest;
import com.example.Backend.response.AuthResponse;
import com.example.Backend.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
public class AuthenticationController {


    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody AuthRequest authRequestDTO) throws Exception{
        try {
            AuthResponse authResponseDTO= userService.signIn(authRequestDTO);
            System.out.println(SecurityContextHolder.getContext());
            return new ResponseEntity<>(authResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/admin/login")
    public ResponseEntity<?> signInAdmin(@RequestBody AuthRequest authRequestDTO) throws Exception{
        try {
            AuthResponse authResponseDTO= userService.signInAdmin(authRequestDTO);
            System.out.println(SecurityContextHolder.getContext());
            return new ResponseEntity<>(authResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            // Remove "Bearer " prefix from token
            String jwtToken = token.substring(7);
            userService.logout(jwtToken);
            return ResponseEntity.ok().body("Logout successful!");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@RequestBody UserRequest userRequest) {
        try {
            AuthResponse response = userService.createUser(userRequest);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (BadRequestException | MessagingException e) {
            return new ResponseEntity<>(new AuthResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/verify-email/{token}")
    public ModelAndView confirmEmail(@PathVariable("token") String token) {
        try{
            String result = userService.confirmEmail(token);
            return new ModelAndView("confirm-success");
        } catch (Exception e) {
            return new ModelAndView("confirm-fail");
        }
    }



}

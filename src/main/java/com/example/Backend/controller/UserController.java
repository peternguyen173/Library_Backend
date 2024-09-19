package com.example.Backend.controller;

import com.example.Backend.entity.User;
import com.example.Backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String authorizationHeader){
        try {
            String jwt = authorizationHeader.startsWith("Bearer ") ?
                    authorizationHeader.substring(7) : authorizationHeader;

            if (jwt == null || jwt.isEmpty()) {
                throw new IllegalArgumentException("JWT is missing");
            }

            User user = userService.findUserByJwt(jwt);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid JWT: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @PutMapping("/users/change-password")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String authorizationHeader,@RequestBody String newPassword) {
        try{
            String jwt = authorizationHeader.startsWith("Bearer ") ?
                    authorizationHeader.substring(7) : authorizationHeader;

            if (jwt == null || jwt.isEmpty()) {
                throw new IllegalArgumentException("JWT is missing");
            }
            User user = userService.changePassword(jwt, newPassword);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid JWT: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}

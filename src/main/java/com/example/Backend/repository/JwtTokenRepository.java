package com.example.Backend.repository;

import com.example.Backend.entity.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {

    @Query("DELETE FROM JwtToken b WHERE b.expirationTime < :now")
    void deleteExpiredTokens(LocalDateTime now);

    boolean existsByToken(String token);

    Optional<JwtToken> findByToken(String token);

}

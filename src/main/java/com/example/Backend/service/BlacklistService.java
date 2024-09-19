package com.example.Backend.service;

import com.example.Backend.entity.JwtToken;
import com.example.Backend.exception.NotFoundException;
import com.example.Backend.repository.JwtTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class BlacklistService {

    @Autowired
    private JwtTokenRepository tokenRepository;

    public void addToBlacklist(String token) {
        JwtToken blacklistToken = new JwtToken();
        blacklistToken.setToken(token);
        blacklistToken.setExpirationTime(LocalDateTime.now().plusDays(1));
        tokenRepository.save(blacklistToken);
    }

    public boolean isBlacklisted(String token) {
        return tokenRepository.existsByToken(token);
    }

    public void removeFromBlacklist(String token) throws NotFoundException {
        JwtToken blacklistToken = tokenRepository.findByToken(token).orElseThrow(()-> new NotFoundException("Token not found!"));
    }

    @Scheduled(fixedRate = 3600000)// Chạy mỗi giờ
    public void cleanUpBlacklist() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteExpiredTokens(now);
    }

}

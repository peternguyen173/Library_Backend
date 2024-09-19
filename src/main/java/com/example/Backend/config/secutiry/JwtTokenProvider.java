package com.example.Backend.config.secutiry;

import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {
    private final String JWT_SECRET = "======================JWT12345=Spring===========================";

    private final long JWT_EXPIRATION_TIME = 86400000;


    public String generateToken(Authentication auth) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + JWT_EXPIRATION_TIME);

        // Chuyển đổi danh sách vai trò thành chuỗi không có dấu ngoặc vuông
        String roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Lấy vai trò là chuỗi
                .collect(Collectors.joining(",")); // Nối vai trò bằng dấu phẩy

        return Jwts.builder()
                .setSubject("Library_App")
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .claim("email", auth.getName())
                .claim("role", roles) // Lưu vai trò dưới dạng chuỗi không có dấu ngoặc vuông
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }


    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody();
        return String.valueOf(claims.get("email"));
    }


    public boolean validateToken(String authToken) {
        try{
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            return true;
        }
        catch(MalformedJwtException e) {
            log.error("Invalid JWT token");
        }
        catch(ExpiredJwtException e) {
            log.error("Expired JWT token");
        }
        catch(UnsupportedJwtException e) {
            log.error("Unsupported JWT token");
        }
        catch (IllegalArgumentException e){
            log.error("JWT claims string is empty");
        }
        return false;
    }

}

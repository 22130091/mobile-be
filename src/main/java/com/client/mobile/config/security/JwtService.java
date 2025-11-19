package com.client.mobile.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct; // Import quan trọng
import org.springframework.beans.factory.annotation.Value; // Import quan trọng
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

//@Service
//public class JwtService {
//
//    @Value("${jwt.secret-key}")
//    private String SECRET_KEY;
//
//    private Key key;
//
//    //   Dùng @PostConstruct để khởi tạo 'key'
//    // Phương thức này sẽ chạy sau khi SECRET_KEY đã được tiêm vào
//    @PostConstruct
//    public void initKey() {
//        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
//    }
//
//    public String generateToken(String username) {
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 giờ
//                .signWith(key)
//                .compact();
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//
//    public boolean isTokenValid(String token) {
//        try {
//            extractAllClaims(token);
//            return true;
//        } catch (JwtException e) {
//            return false;
//        }
//    }
//
//    public Instant extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration).toInstant();
//    }
//}


@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    private Key key;

    @PostConstruct
    public void initKey() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        String tokenId = UUID.randomUUID().toString();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setId(tokenId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 giờ
                .signWith(key)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenId(String token) {
        return extractClaim(token, Claims::getId);
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Instant extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration).toInstant();
    }
}

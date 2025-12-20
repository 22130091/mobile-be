package com.client.mobile.config.security;

import com.client.mobile.dto.response.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct; // Import quan trọng
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value; // Import quan trọng
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.expiration:3600000}")
    private long jwtExpiration;

    @Getter
    @Value("${jwt.refresh-token.expiration:604800000}")
    private long refreshExpiration;

    private Key key;

    @PostConstruct
    public void initKey() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public TokenResponse generateToken(String username, List<String> roles ) {
        String accessToken = generateAccessToken(username, roles);
        String refreshToken = generateRefreshToken(username);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }



    private String generateRefreshToken(String username) {
        String tokenId=UUID.randomUUID().toString();
        return buildToken(new HashMap<>(), username, tokenId, refreshExpiration);
    }

    public String generateAccessToken(String username, List<String> roles) {
        Map<String,Object> claim =new HashMap<>();
        claim.put("roles",roles);
        String tokenId=UUID.randomUUID().toString();
        return buildToken(claim, username, tokenId, jwtExpiration);

    }

    private String buildToken(Map<String, Object> claims, String username, String tokenId, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setId(tokenId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    /**
     * gọi builder của jwt để thao tác với token
     * thêm key vào phần kiểm tra
     *parseclaim để  lấy header và payload trong token. so sánh chữ kí cũ và mới
     */
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


    public Instant extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration).toInstant();
    }

    public boolean isTokenValid(String token, String username){
        final String tokenFromExtracName=extractUsername(username);
        return (tokenFromExtracName.equals(token))&&(!isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).isBefore(Instant.now());
    }

}

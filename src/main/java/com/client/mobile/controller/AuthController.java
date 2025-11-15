package com.client.mobile.controller;

import com.client.mobile.config.redis.RedisTokenService;
import com.client.mobile.config.security.JwtService;
import com.client.mobile.config.security.TokenBlacklistService;
import com.client.mobile.process_facade.AccountProfileFacade;
import com.client.mobile.request.CreateAccountRequest;
import com.client.mobile.request.LoginRequest; // Đảm bảo DTO này có getFullName()
import com.client.mobile.response.AccountResponse;
import com.client.mobile.response.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/auth") // Endpoint công khai
public class AuthController {

    private final AccountProfileFacade accountProfileFacade;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
//    private final TokenBlacklistService tokenBlacklistService;
    private  final RedisTokenService redisTokenService;

    public AuthController(AccountProfileFacade accountProfileFacade,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          TokenBlacklistService tokenBlacklistService,
                          RedisTokenService redisTokenService) {
        this.accountProfileFacade = accountProfileFacade;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
//        this.tokenBlacklistService = tokenBlacklistService;
        this.redisTokenService = redisTokenService;
    }


    @PostMapping("/register")
    public ResponseEntity<AccountResponse> registerAccount(
            @RequestBody CreateAccountRequest dto) {

        AccountResponse newAccount = accountProfileFacade.createAccount(dto);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }

    /**
     * Endpoint để đăng nhập và nhận về JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getFullName(),
                            request.getPassword()
                    )
            );

            String username = authentication.getName();
            String token = jwtService.generateToken(username);
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }
//    @DeleteMapping("/logout")
//    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.badRequest().body("Token không hợp lệ");
//        }
//
//        String token = authHeader.substring(7);
//        Instant expiry = jwtService.extractExpiration(token);
//        tokenBlacklistService.blacklistToken(token, expiry);
//
//        return ResponseEntity.ok("Đăng xuất thành công");
//    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String tokenId = jwtService.extractTokenId(token);
        redisTokenService.blacklistToken(tokenId, 3600); // TTL = 1h
        return ResponseEntity.ok("Đăng xuất thành công");
    }

    // Logout tất cả thiết bị
    @DeleteMapping("/logout-all")
    public ResponseEntity<String> logoutAllDevices(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        redisTokenService.getAllTokens(username).forEach(tid ->
                redisTokenService.blacklistToken(tid.toString(), 3600)
        );

        redisTokenService.removeAllTokens(username);
        return ResponseEntity.ok("Đăng xuất tất cả thiết bị thành công");
    }

}
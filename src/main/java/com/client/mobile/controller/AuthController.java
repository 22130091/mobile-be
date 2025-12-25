package com.client.mobile.controller;

import com.client.mobile.config.redis.RedisTokenService;
import com.client.mobile.config.security.JwtService;
import com.client.mobile.dto.request.*;
import com.client.mobile.dto.response.AccountResponse;
import com.client.mobile.dto.response.TokenResponse;
import com.client.mobile.entity.Account;
import com.client.mobile.entity.RefreshToken;
import com.client.mobile.entity.Role;
import com.client.mobile.process_facade.AccountProfileFacade;
import com.client.mobile.repository.AccountRepository;
import com.client.mobile.repository.RefreshTokenRepository;
import com.client.mobile.service.OtpSender;
import com.client.mobile.service.imp.AuthService;
import com.client.mobile.service.imp.RefreshTokenService;
import com.client.mobile.service.imp.SmsOtpSender;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AccountProfileFacade accountProfileFacade;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountRepository accountRepository;
    private final AuthService authService;
    private final SmsOtpSender smsOtpSender;
    private final OtpSender emailOtpSender;


    @PostMapping("/register")
    public ResponseEntity<AccountResponse> registerAccount(@RequestBody CreateAccountRequest dto) {
        AccountResponse newAccount = accountProfileFacade.createAccount(dto);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest servletRequest) { // Thêm servletRequest để lấy User-Agent
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getFullName(),
                            request.getPassword()
                    )
            );
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String username = authentication.getName();
            String userAgent = servletRequest.getHeader("User-Agent");
            String deviceInfo = (userAgent != null) ? userAgent : "Unknown Device";

            TokenResponse tokenResponse = jwtService.generateToken(username, roles);
            long accessExpire = jwtService.extractExpiration(tokenResponse.getAccessToken()).getEpochSecond() - Instant.now().getEpochSecond();
            redisTokenService.saveUserToken(username, tokenResponse.getAccessToken(), accessExpire);
            refreshTokenService.saveRefreshTokenToDb(
                    tokenResponse.getRefreshToken(),
                    username,
                    deviceInfo,
                    jwtService.getRefreshExpiration()
            );
            return ResponseEntity.ok(tokenResponse);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPass(@RequestBody ForgotPasswordRequest request) {
        String email = request.getEmail();
        Random random = new Random();
        String otp = String.valueOf(random.nextInt(900000) + 100000);
        redisTokenService.saveOtp(email, otp);
        emailOtpSender.sendOtp(email, otp);
        return ResponseEntity.ok("Đã gửi email đặt lại mật khẩu");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
            return ResponseEntity.ok("Đổi mật khẩu thành công. Vui lòng đăng nhập lại.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        String username = jwtService.extractUsername(requestRefreshToken);
        RefreshToken storedToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại trong DB"));

        if (storedToken.isRevoked()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh token đã bị thu hồi");
        }
        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh token đã hết hạn");
        }
        Account account = accountRepository.findByFullName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = account.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
        String newAccessToken = jwtService.generateAccessToken(username, roles);
        long accessExpire = jwtService.extractExpiration(newAccessToken).getEpochSecond() - Instant.now().getEpochSecond();
        redisTokenService.saveUserToken(username, newAccessToken, accessExpire);
        TokenResponse response = TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(requestRefreshToken)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/login-success")
    public ResponseEntity<String> loginSuccess(
            @RequestParam("accessToken") String accessToken,
            @RequestParam("refreshToken") String refreshToken) {
        return ResponseEntity.ok("Đăng nhập Google thành công! \n\nAccessToken: " + accessToken + "\n\nRefreshToken: " + refreshToken);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String tokenId = jwtService.extractTokenId(token);

        redisTokenService.blacklistToken(tokenId, 3600);
        return ResponseEntity.ok("Đăng xuất thành công");
    }

    @DeleteMapping("/logout-all")
    public ResponseEntity<String> logoutAllDevices(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        redisTokenService.getAllTokens(username).forEach(tid ->
                redisTokenService.blacklistToken(tid.toString(), 3600)
        );
        redisTokenService.removeAllTokens(username);
        refreshTokenService.revokeAllUserTokens(username);

        return ResponseEntity.ok("Đăng xuất tất cả thiết bị thành công");
    }


    @PostMapping("/verify-sms-otp")
    public ResponseEntity<?> verifySmsOtp(@RequestBody VerifySmsRequest request) {
        try {
            String rawPhoneNumber = smsOtpSender.verifyFirebaseToken(request.getIdToken());
            final String finalPhoneNumber;

            if (rawPhoneNumber.startsWith("+84")) {
                finalPhoneNumber = "0" + rawPhoneNumber.substring(3);
            } else {
                finalPhoneNumber = rawPhoneNumber;
            }
            Account account = accountRepository.findByPhone(finalPhoneNumber)
                    .orElseThrow(() -> new RuntimeException("Số điện thoại " + finalPhoneNumber + " chưa đăng ký"));

            String internalOtp = String.valueOf(new Random().nextInt(900000) + 100000);
            redisTokenService.saveOtp(account.getEmail(), internalOtp);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Lỗi: " + e.getMessage());
        }
    }
}
package com.client.mobile.service.imp;

import com.client.mobile.entity.Account;
import com.client.mobile.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;

import com.client.mobile.config.redis.RedisTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final RedisOtpService redisOtpService;
    private final PasswordEncoder passwordEncoder;
    private final RedisTokenService redisTokenService;
    @Transactional
    public void resetPassword(String emailInput, String otpInput, String newPassword) {
        Account account = accountRepository.findByEmail(emailInput)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống."));
        String storedOtp = redisTokenService.getOtp(emailInput);
        System.out.println(storedOtp);
        if (storedOtp != null && storedOtp.equals(otpInput.trim())) {
            account.setPassword(passwordEncoder.encode(newPassword));
            accountRepository.save(account);
            redisTokenService.deleteOtp(emailInput);
        } else {
            throw new RuntimeException("Mã xác thực không đúng hoặc đã hết hạn.");
        }
    }
}
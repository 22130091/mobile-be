package com.client.mobile.service.imp;

import com.client.mobile.entity.Account;
import com.client.mobile.entity.RefreshToken;
import com.client.mobile.repository.AccountRepository;
import com.client.mobile.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountRepository accountRepository;

    public void saveRefreshTokenToDb(String token, String username, String deviceInfo, long expirationMs) {
        Account account = accountRepository.findByFullName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(token);
        refreshTokenEntity.setAccount(account);
        refreshTokenEntity.setDeviceInfo(deviceInfo);
        refreshTokenEntity.setExpiryDate(Instant.now().plusMillis(expirationMs));
        refreshTokenEntity.setRevoked(false);

        refreshTokenRepository.save(refreshTokenEntity);
    }

    @Transactional
    public void revokeAllUserTokens(String username) {
        List<RefreshToken> validTokens = refreshTokenRepository.findByAccount_FullName(username);
        if (validTokens.isEmpty()) {
            return;
        }
        validTokens.forEach(token -> {
            token.setRevoked(true);
        });
        refreshTokenRepository.saveAll(validTokens);
    }
}
package com.client.mobile.config.security;

import com.client.mobile.config.redis.RedisTokenService;
import com.client.mobile.dto.response.TokenResponse;
import com.client.mobile.entity.Account;
import com.client.mobile.entity.Role;
import com.client.mobile.repository.AccountRepository;
import com.client.mobile.repository.RoleRepository;
import com.client.mobile.service.imp.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        String userAgent = request.getHeader("User-Agent");
        String deviceInfo = (userAgent != null && !userAgent.isEmpty()) ? userAgent : "Unknown Device";

        Optional<Account> accountOptional = accountRepository.findByFullName(name);
        Account account;
        if (accountOptional.isPresent()) {
            account = accountOptional.get();
        } else {
            Role userRole = roleRepository.findByRoleName("ROLE_USER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setRoleName("ROLE_USER");
                        return roleRepository.save(newRole);
                    });

            account = new Account();
            account.setEmail(email);
            account.setFullName(name);
            account.setPassword("");
            account.setStatus("active");
            account.setRoles(new HashSet<>(Collections.singletonList(userRole)));
            accountRepository.save(account);
        }

        List<String> roles = new ArrayList<>();
        if (account.getRoles() != null) {
            roles = account.getRoles().stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toList());
        }

        TokenResponse tokenResponse = jwtService.generateToken(account.getFullName(), roles);
        String accessToken = tokenResponse.getAccessToken();
        String refreshToken = tokenResponse.getRefreshToken();

        long expirationTime = jwtService.extractExpiration(accessToken).getEpochSecond() - Instant.now().getEpochSecond();
        redisTokenService.saveUserToken(account.getFullName(), accessToken, expirationTime);
        refreshTokenService.saveRefreshTokenToDb(
                refreshToken,
                account.getFullName(),
                deviceInfo,
                jwtService.getRefreshExpiration()
        );

        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:9090/index.html")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
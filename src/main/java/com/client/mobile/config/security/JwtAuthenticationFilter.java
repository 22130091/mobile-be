//package com.client.mobile.config.security;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//    private final TokenBlacklistService tokenBlacklistService;
//
//    public JwtAuthenticationFilter(JwtService jwtService, TokenBlacklistService tokenBlacklistService) {
//        this.jwtService = jwtService;
//        this.tokenBlacklistService = tokenBlacklistService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//
//            if (tokenBlacklistService.isBlacklisted(token)) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.setContentType("application/json; charset=utf-8");
//                String jsonErrorResponse = "{\"error\": \"Token đã bị thu hồi\", \"message\": \"Token đã bị thu hồi\"}";
//                response.getWriter().write(jsonErrorResponse);
//                return;
//            }
//
//            String username = jwtService.extractUsername(token);
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(username, null, null);
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}

package com.client.mobile.config.security;



import com.client.mobile.config.redis.RedisTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;
    private final CustomUserDetailsService userDetailsService;

    // Danh sách các endpoint công khai không cần JWT
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password",
            "/api/v1/auth/login-success",
            "/api/payment/vnpay-return",
            "/oauth2/",
            "/login/"
    );

    public JwtAuthenticationFilter(JwtService jwtService, RedisTokenService redisTokenService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.redisTokenService = redisTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException {

        String requestPath = request.getRequestURI();

        // Bỏ qua các endpoint công khai
        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // Lấy jti từ token
                String tokenId = jwtService.extractTokenId(token);

                if (redisTokenService.isBlacklisted(tokenId)) {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                            "Token đã bị thu hồi", "Token đã bị đăng xuất hoặc thu hồi");
                    return;
                }

                String username = jwtService.extractUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    try {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        redisTokenService.saveUserToken(username, token, jwtService.extractExpiration(token).getEpochSecond() - Instant.now().getEpochSecond());
                    } catch (UsernameNotFoundException e) {
                        logger.warn("User not found: {}", username);
                        sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                                "User not found", "Người dùng không tồn tại");
                        return;
                    }
                }
            } catch (ExpiredJwtException e) {
                logger.warn("JWT token expired: {}", e.getMessage());
                Date expiration = e.getClaims().getExpiration();
                String expirationTime = formatDate(expiration);
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Token đã hết hạn",
                        "Token đã hết hạn vào lúc: " + expirationTime + ". Vui lòng đăng nhập lại.");
                return;
            } catch (MalformedJwtException e) {
                logger.warn("Invalid JWT token: {}", e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Token không hợp lệ", "Token có định dạng không đúng");
                return;
            } catch (SignatureException e) {
                logger.warn("JWT signature validation failed: {}", e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Token không hợp lệ", "Chữ ký token không đúng");
                return;
            } catch (JwtException e) {
                logger.warn("JWT error: {}", e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Token lỗi", e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestPath) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(requestPath::startsWith);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json; charset=utf-8");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(),
                java.util.Map.of(
                        "status", status,
                        "error", error,
                        "message", message,
                        "timestamp", Instant.now().toString()
                ));
    }

    private String formatDate(Date date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        return formatter.format(date.toInstant());
    }
}

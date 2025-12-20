package com.client.mobile.dto.response;

public class AuthResponse {

    private final String accessToken;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getToken() {
        return accessToken;
    }
}
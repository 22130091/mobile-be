package com.client.mobile.response;

public class AuthResponse {

    private final String accessToken;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getToken() {
        return accessToken;
    }
}
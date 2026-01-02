package com.example.healthapp.dto;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;

    public LoginResponse(String accessToken, String refreshToken, long expiresIn) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}

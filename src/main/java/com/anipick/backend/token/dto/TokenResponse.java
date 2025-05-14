package com.anipick.backend.token.dto;

import lombok.Getter;

@Getter
public class TokenResponse {
    private final String accessToken;
    private final String refreshToken;

    private TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static TokenResponse fromPairToken(String accessToken, String refreshToken) {
        return new TokenResponse(accessToken, refreshToken);
    }
}

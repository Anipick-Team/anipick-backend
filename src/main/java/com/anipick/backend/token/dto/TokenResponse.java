package com.anipick.backend.token.dto;

import lombok.Getter;

@Getter
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    public static TokenResponse fromPairToken(String accessToken, String refreshToken) {
        TokenResponse response = new TokenResponse();
        response.accessToken = accessToken;
        response.refreshToken = refreshToken;

        return response;
    }
}

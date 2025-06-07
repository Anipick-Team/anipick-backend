package com.anipick.backend.token.dto;

import lombok.Getter;

@Getter
public class LoginResponse {
    private final Boolean reviewCompletedYn;
    private final TokenResponse token;

    private LoginResponse(Boolean reviewCompletedYn, TokenResponse tokenResponse) {
        this.reviewCompletedYn = reviewCompletedYn;
        this.token = tokenResponse;
    }

    public static LoginResponse from(Boolean reviewCompletedYn, TokenResponse tokenResponse) {
        return new LoginResponse(reviewCompletedYn, tokenResponse);
    }
}

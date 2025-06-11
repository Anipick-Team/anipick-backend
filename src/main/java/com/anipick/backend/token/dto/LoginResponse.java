package com.anipick.backend.token.dto;

import lombok.Getter;

@Getter
public class LoginResponse {
    private final Boolean reviewCompletedYn;
    private final Long userId;
    private final String nickname;
    private final TokenResponse token;

    private LoginResponse(Boolean reviewCompletedYn, Long userId, String nickname, TokenResponse tokenResponse) {
        this.reviewCompletedYn = reviewCompletedYn;
        this.userId = userId;
        this.nickname = nickname;
        this.token = tokenResponse;
    }

    public static LoginResponse from(Boolean reviewCompletedYn, Long userId, String nickname, TokenResponse tokenResponse) {
        return new LoginResponse(reviewCompletedYn, userId, nickname, tokenResponse);
    }
}

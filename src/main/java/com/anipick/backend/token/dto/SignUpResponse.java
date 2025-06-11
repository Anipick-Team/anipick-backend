package com.anipick.backend.token.dto;

import lombok.Getter;

@Getter
public class SignUpResponse {
    private final Boolean reviewCompletedYn;
    private final Long userId;
    private final String nickname;
    private final TokenResponse token;

    private SignUpResponse(Boolean reviewCompletedYn, Long userId, String nickname, TokenResponse tokenResponse) {
        this.reviewCompletedYn = reviewCompletedYn;
        this.userId = userId;
        this.nickname = nickname;
        this.token = tokenResponse;
    }

    public static SignUpResponse from(Boolean reviewCompletedYn, Long userId, String nickname, TokenResponse tokenResponse) {
        return new SignUpResponse(reviewCompletedYn, userId, nickname, tokenResponse);
    }
}

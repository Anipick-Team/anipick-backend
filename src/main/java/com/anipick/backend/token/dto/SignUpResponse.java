package com.anipick.backend.token.dto;

import lombok.Getter;

@Getter
public class SignUpResponse {
    private final TokenResponse token;

    private SignUpResponse(TokenResponse tokenResponse) {
        this.token = tokenResponse;
    }

    public static SignUpResponse from(TokenResponse tokenResponse) {
        return new SignUpResponse(tokenResponse);
    }
}

package com.anipick.backend.token.dto;

import lombok.Getter;

@Getter
public class RefreshTokenRequest {
    private String refreshToken;
}

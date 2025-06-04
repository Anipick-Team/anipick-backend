package com.anipick.backend.oauth.dto;

import lombok.Getter;

@Getter
public class SocialLoginRequest {
    private String platform;
    private String code;
}

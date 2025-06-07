package com.anipick.backend.auth.dto;

import lombok.Getter;

@Getter
public class AuthCodeRequest {
    private String email;
    private String code;
}

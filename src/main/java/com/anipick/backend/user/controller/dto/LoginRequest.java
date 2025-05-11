package com.anipick.backend.user.controller.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;
    private String password;
}

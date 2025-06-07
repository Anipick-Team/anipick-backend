package com.anipick.backend.auth.dto;

import lombok.Getter;

@Getter
public class AuthPasswordRequest {
    private String email;
    private String newPassword;
    private String checkNewPassword;
}

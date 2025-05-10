package com.anipick.backend.user.controller.dto;

import lombok.Getter;

@Getter
public class SignUpRequest {
    private String email;
    private String password;
    private Boolean termsAndConditions;
}

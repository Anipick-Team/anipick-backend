package com.anipick.backend.user.dto;

import lombok.Getter;

@Getter
public class SignUpRequest {
    private String email;
    private String password;
    private Boolean termsAndConditions;
}

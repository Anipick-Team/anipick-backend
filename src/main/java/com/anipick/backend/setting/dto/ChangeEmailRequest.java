package com.anipick.backend.setting.dto;

import lombok.Getter;

@Getter
public class ChangeEmailRequest {
    private String newEmail;
    private String password;
}

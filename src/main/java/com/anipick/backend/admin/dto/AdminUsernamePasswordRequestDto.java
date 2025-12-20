package com.anipick.backend.admin.dto;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class AdminUsernamePasswordRequestDto {
    private String username;
    private String password;

    public static boolean checkValidUsernameAndPassword(String username, String password) {
        boolean hasTextUsername = StringUtils.hasText(username);
        boolean hasTextPassword = StringUtils.hasText(password);
        return hasTextUsername && hasTextPassword;
    }
}

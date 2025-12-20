package com.anipick.backend.admin.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Admin {
    private Long adminId;
    private String username;
    private String password;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public Admin(String username, String password, Boolean isActive, LocalDateTime createdAt, LocalDateTime lastLoginAt) {
        this.username = username;
        this.password = password;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
    }

    public static Admin createEncoderPasswordAccount(String username, String password) {
        return new Admin(username, password, true, LocalDateTime.now(), null);
    }
}

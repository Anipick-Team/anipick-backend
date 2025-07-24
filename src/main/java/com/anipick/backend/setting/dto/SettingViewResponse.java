package com.anipick.backend.setting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SettingViewResponse {
    private String nickname;
    private String email;
    private String provider;

    public static SettingViewResponse from(String nickname, String email, String provider) {
        return new SettingViewResponse(nickname, email, provider);
    }
}

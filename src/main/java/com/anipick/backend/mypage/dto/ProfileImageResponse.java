package com.anipick.backend.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileImageResponse {
    private String profileImageUrl;

    public static ProfileImageResponse from(String profileImageUrl) {
        return new ProfileImageResponse(profileImageUrl);
    }
}

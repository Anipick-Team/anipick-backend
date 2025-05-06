package com.anipick.backend.user.dto;

import com.anipick.backend.user.domain.LoginFormat;
import com.anipick.backend.user.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SignUpResponse {
    private Long userId;
    private String email;
    private String password;
    private String nickname;
    private String profileImageUrl;
    private LoginFormat loginFormat;
    private Boolean termsAndConditions;
    private Boolean adultYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static SignUpResponse from(User user) {
        SignUpResponse response = new SignUpResponse();
        response.userId =  user.getUserId();
        response.email = user.getEmail();
        response.password = user.getPassword();
        response.nickname = user.getNickname();
        response.profileImageUrl = user.getProfileImageUrl();
        response.loginFormat = user.getLoginFormat();
        response.termsAndConditions = user.getTermsAndConditions();
        response.adultYn = user.getAdultYn();
        response.createdAt = user.getCreatedAt();
        response.updatedAt = user.getUpdatedAt();
        response.deletedAt = user.getDeletedAt();

        return response;
    }
}

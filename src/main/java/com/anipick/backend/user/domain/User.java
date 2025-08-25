package com.anipick.backend.user.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class User {
    private Long userId;
    private String email;
    private String password;
    private String nickname;
    private LoginFormat loginFormat;
    private Boolean termsAndConditions;
    private Boolean adultYn;
    private Boolean reviewCompletedYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}

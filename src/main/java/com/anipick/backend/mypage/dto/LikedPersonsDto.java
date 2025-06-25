package com.anipick.backend.mypage.dto;

import lombok.Getter;

@Getter
public class LikedPersonsDto {
    private Long personId;
    private String name;
    private String profileImageUrl;
}

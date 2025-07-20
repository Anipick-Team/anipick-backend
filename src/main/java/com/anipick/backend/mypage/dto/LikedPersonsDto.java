package com.anipick.backend.mypage.dto;

import lombok.Getter;

@Getter
public class LikedPersonsDto {
    private Long personId;
    private Long userLikedVoiceActorId;
    private String name;
    private String profileImageUrl;
}

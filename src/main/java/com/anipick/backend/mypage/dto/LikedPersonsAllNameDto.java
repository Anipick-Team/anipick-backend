package com.anipick.backend.mypage.dto;

import lombok.Getter;

@Getter
public class LikedPersonsAllNameDto {
    private Long personId;
    private Long userLikedVoiceActorId;
    private String nameKor;
    private String nameEng;
    private String profileImageUrl;
}

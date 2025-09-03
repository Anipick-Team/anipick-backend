package com.anipick.backend.mypage.dto;

import lombok.Getter;

@Getter
public class LikedAnimesAllTitleDto {
    private Long animeId;
    private Long animeLikeId;
    private String titleKor;
    private String titleEng;
    private String titleRom;
    private String titleNat;
    private String coverImageUrl;
}

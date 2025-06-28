package com.anipick.backend.mypage.dto;

import lombok.Getter;

@Getter
public class LikedAnimesDto {
    private Long animeId;
    private Long animeLikeId;
    private String title;
    private String coverImageUrl;
}

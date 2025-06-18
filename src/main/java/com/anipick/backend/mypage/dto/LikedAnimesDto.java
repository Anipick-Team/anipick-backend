package com.anipick.backend.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikedAnimesDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
}

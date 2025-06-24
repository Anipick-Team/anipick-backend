package com.anipick.backend.mypage.dto;

import lombok.Getter;

@Getter
public class WatchListAnimesDto {
    private Long animeId;
    private Long userAnimeStatusId;
    private String title;
    private String coverImageUrl;
}

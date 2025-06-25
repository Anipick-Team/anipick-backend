package com.anipick.backend.mypage.dto;

import lombok.Getter;

@Getter
public class FinishedAnimesDto {
    private Long animeId;
    private Long userAnimeStatusId;
    private String title;
    private String coverImageUrl;
    private Double myRating;
}

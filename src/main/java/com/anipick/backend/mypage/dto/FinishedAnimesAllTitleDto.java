package com.anipick.backend.mypage.dto;

import lombok.Getter;

@Getter
public class FinishedAnimesAllTitleDto {
    private Long animeId;
    private Long userAnimeStatusId;
    private String titleKor;
    private String titleEng;
    private String titleRom;
    private String titleNat;
    private String coverImageUrl;
    private Double myRating;
}

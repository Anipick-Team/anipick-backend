package com.anipick.backend.mypage.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class FinishedAnimesAllTitleDto {
    private Long animeId;
    private Long userAnimeStatusId;
    private String titleMan;
    private String titleKor;
    private String titleEng;
    private String titleRom;
    private String titleNat;
    private String coverImageUrl;
    private Double myRating;
    private LocalDateTime sortedAt;
    private LocalDate startDate;
}

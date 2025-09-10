package com.anipick.backend.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnimeAllTitleItemRecommendTagCountDto {
    private Long animeId;
    private String titleKor;
    private String titleEng;
    private String titleRom;
    private String titleNat;
    private String coverImage;
    private Long tagCount;
}

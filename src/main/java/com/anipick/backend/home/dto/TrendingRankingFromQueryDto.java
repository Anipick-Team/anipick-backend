package com.anipick.backend.home.dto;

import lombok.Getter;

@Getter
public class TrendingRankingFromQueryDto {
    private Long animeId;
    private String titleKor;
    private String titleEng;
    private String coverImageUrl;
}

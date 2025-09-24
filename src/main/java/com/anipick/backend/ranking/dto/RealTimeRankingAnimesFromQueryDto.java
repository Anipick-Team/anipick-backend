package com.anipick.backend.ranking.dto;

import lombok.Getter;


@Getter
public class RealTimeRankingAnimesFromQueryDto {
    private Long animeId;
    private String titleKor;
    private String titleEng;
    private String titleRom;
    private String titleNat;
    private String coverImageUrl;
    private Long trending;
    private Long popularity;
}

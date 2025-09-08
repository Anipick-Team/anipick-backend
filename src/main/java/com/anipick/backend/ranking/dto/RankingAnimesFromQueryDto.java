package com.anipick.backend.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankingAnimesFromQueryDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private Long popularity;
}

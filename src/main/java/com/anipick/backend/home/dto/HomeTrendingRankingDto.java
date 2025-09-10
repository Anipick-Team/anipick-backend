package com.anipick.backend.home.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HomeTrendingRankingDto {
    private Long animeId;
    private String title;
    private Long rank;
    private String coverImageUrl;

    public static HomeTrendingRankingDto of(TrendingRankingFromQueryDto dto, Long rank) {
        return new HomeTrendingRankingDto(
                dto.getAnimeId(),
                dto.getTitle(),
                rank,
                dto.getCoverImageUrl()
        );
    }
}

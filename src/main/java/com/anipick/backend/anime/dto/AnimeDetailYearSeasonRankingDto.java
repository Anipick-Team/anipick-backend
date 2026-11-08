package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 애니 상세의 연도-분기 별 랭킹 순위
 */
@Getter
@AllArgsConstructor
public class AnimeDetailYearSeasonRankingDto {
    private Integer year;
    private Integer season;
    private Long rank;

    public static AnimeDetailYearSeasonRankingDto of(Integer year, Integer season, Long rank) {
        return new AnimeDetailYearSeasonRankingDto(year, season, rank);
    }
}

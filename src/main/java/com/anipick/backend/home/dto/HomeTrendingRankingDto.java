package com.anipick.backend.home.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HomeTrendingRankingDto {
    private Long animeId;
    private String title;
    private Long rank;
    private String coverImageUrl;
    private Double averageRating;
    private List<String> genres;
    private Long nextEpisode;

    public static HomeTrendingRankingDto of(TrendingRankingFromQueryDto dto, Long rank, List<String> genres) {
        String title = LocalizationUtil.pickTitle(dto.getTitleMan(), dto.getTitleKor(), dto.getTitleEng(), dto.getTitleRom(), dto.getTitleNat());

        return new HomeTrendingRankingDto(
                dto.getAnimeId(),
                title,
                rank,
                dto.getCoverImageUrl(),
                dto.getAverageRating(),
                genres,
                dto.getNextEpisode()
        );
    }
}

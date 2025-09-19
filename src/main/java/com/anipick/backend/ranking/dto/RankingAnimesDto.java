package com.anipick.backend.ranking.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import com.anipick.backend.ranking.domain.Trend;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RankingAnimesDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private Long rank;
    private List<String> genres;
    private Long popularity;

    public static RankingAnimesDto from(Long displayRank, RankingAnimesFromQueryDto dto, List<String> genres) {
        String title = LocalizationUtil.pickTitle(
                dto.getTitleKor(),
                dto.getTitleEng(),
                dto.getTitleRom(),
                dto.getTitleNat()
        );

        return new RankingAnimesDto(
                dto.getAnimeId(),
                title,
                dto.getCoverImageUrl(),
                displayRank,
                genres.stream()
                        .limit(3)
                        .toList(),
                dto.getPopularity()
        );
    }
}

package com.anipick.backend.home.dto;

import com.anipick.backend.common.util.LocalizationUtil;
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
        String title = LocalizationUtil.pickTitle(dto.getTitleKor(), dto.getTitleEng(), dto.getTitleRom(), dto.getTitleNat());

        return new HomeTrendingRankingDto(
                dto.getAnimeId(),
                title,
                rank,
                dto.getCoverImageUrl()
        );
    }
}

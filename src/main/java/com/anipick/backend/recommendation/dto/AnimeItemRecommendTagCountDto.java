package com.anipick.backend.recommendation.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnimeItemRecommendTagCountDto {
    private Long animeId;
    private String title;
    private String coverImage;
    private Long tagCount;

    public static AnimeItemRecommendTagCountDto animeTitleTranslationPick(AnimeAllTitleItemRecommendTagCountDto dto) {
        String title = LocalizationUtil.pickTitle(
                dto.getTitleKor(),
                dto.getTitleEng(),
                dto.getTitleRom(),
                dto.getTitleNat()
        );
        return new AnimeItemRecommendTagCountDto(dto.getAnimeId(), title, dto.getCoverImage(), dto.getTagCount());
    }
}

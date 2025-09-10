package com.anipick.backend.explore.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExploreItemDto {
    private Long id;
    private String title;
    private String coverImageUrl;
    private Double averageScore;
    private Long score;

    public static ExploreItemDto animeTitleTranslationPick(ExploreAllTitleItemDto dto) {
        String title = LocalizationUtil.pickTitle(
                dto.getTitleKor(),
                dto.getTitleEng(),
                dto.getTitleRom(),
                dto.getTitleNat()
        );
        return new ExploreItemDto(dto.getId(), title, dto.getCoverImageUrl(), dto.getAverageScore(), dto.getScore());
    }
}

package com.anipick.backend.studio.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudioAnimeItemDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private Long seasonYear;

    public static StudioAnimeItemDto animeTitleTranslationPick(StudioAnimeAllTitleItemDto dto) {
        String title = LocalizationUtil.pickTitle(
				dto.getTitleKor(),
				dto.getTitleEng(),
				dto.getTitleRom(),
				dto.getTitleNat()
		);
        return new StudioAnimeItemDto(dto.getAnimeId(), title, dto.getCoverImageUrl(), dto.getSeasonYear());
    }
}

package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WatchListAnimesDto {
    private Long animeId;
    private Long userAnimeStatusId;
    private String title;
    private String coverImageUrl;

    public static WatchListAnimesDto animeTitleTranslationPick(WatchListAnimesAllTitleDto dto) {
        String title = LocalizationUtil.pickTitle(
				dto.getTitleKor(),
				dto.getTitleEng(),
				dto.getTitleRom(),
				dto.getTitleNat()
		);
        return new WatchListAnimesDto(dto.getAnimeId(), dto.getUserAnimeStatusId(), title, dto.getCoverImageUrl());
    }
}

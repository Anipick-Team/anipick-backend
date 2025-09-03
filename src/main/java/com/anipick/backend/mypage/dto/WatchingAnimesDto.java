package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WatchingAnimesDto {
    private Long animeId;
    private Long userAnimeStatusId;
    private String title;
    private String coverImageUrl;

    public static WatchingAnimesDto animeTitleTranslationPick(WatchingAnimesAllTitleDto dto) {
        String title = LocalizationUtil.pickTitle(
				dto.getTitleKor(),
				dto.getTitleEng(),
				dto.getTitleRom(),
				dto.getTitleNat()
		);
        return new WatchingAnimesDto(dto.getAnimeId(), dto.getUserAnimeStatusId(), title, dto.getCoverImageUrl());
    }
}

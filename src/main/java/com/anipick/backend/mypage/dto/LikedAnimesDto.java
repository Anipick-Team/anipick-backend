package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikedAnimesDto {
    private Long animeId;
    private Long animeLikeId;
    private String title;
    private String coverImageUrl;

    public static LikedAnimesDto animeTitleTranslationPick(LikedAnimesAllTitleDto dto) {
        String title = LocalizationUtil.pickTitle(
				dto.getTitleKor(),
				dto.getTitleEng(),
				dto.getTitleRom(),
				dto.getTitleNat()
		);
        return new LikedAnimesDto(dto.getAnimeId(), dto.getAnimeLikeId(), title, dto.getCoverImageUrl());
    }
}

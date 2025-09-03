package com.anipick.backend.mypage.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FinishedAnimesDto {
    private Long animeId;
    private Long userAnimeStatusId;
    private String title;
    private String coverImageUrl;
    private Double myRating;

    public static FinishedAnimesDto animeTitleTranslationPick(FinishedAnimesAllTitleDto dto) {
        String title = LocalizationUtil.pickTitle(
				dto.getTitleKor(),
				dto.getTitleEng(),
				dto.getTitleRom(),
				dto.getTitleNat()
		);
        return new FinishedAnimesDto(dto.getAnimeId(), dto.getUserAnimeStatusId(), title, dto.getCoverImageUrl(), dto.getMyRating());
    }
}

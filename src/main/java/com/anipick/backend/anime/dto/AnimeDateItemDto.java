package com.anipick.backend.anime.dto;

import com.anipick.backend.anime.domain.Anime;
import com.anipick.backend.anime.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AnimeDateItemDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private LocalDate startDate;

    public static AnimeDateItemDto animeTitleTranslationPick(AnimeAlltitleDateDto dto) {
        String title = LocalizationUtil.pickTitle(
				dto.getTitleKor(),
				dto.getTitleEng(),
				dto.getTitleRom(),
				dto.getTitleNat()
		);
        return new AnimeDateItemDto(dto.getAnimeId(), title, dto.getCoverImageUrl(), dto.getStartDate());
    }
}

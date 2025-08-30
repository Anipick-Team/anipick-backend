package com.anipick.backend.anime.dto;

import com.anipick.backend.anime.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnimeItemDto {
	private Long animeId;
	private String title;
	private String coverImageUrl;

	public static AnimeItemDto animeTitleTranslationPick(AnimeAllTitleImgDto dto) {
		String title = LocalizationUtil.pickTitle(
				dto.getTitleKor(),
				dto.getTitleEng(),
				dto.getTitleRom(),
				dto.getTitleNat()
		);
		return new AnimeItemDto(dto.getAnimeId(), title, dto.getCoverImageUrl());
	}
}
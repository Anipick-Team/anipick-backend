package com.anipick.backend.search.dto;

import com.anipick.backend.anime.dto.StudioAllNameItemDto;
import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudioItemDto {
	private Long studioId;
	private String name;

	public static StudioItemDto studioNameTranslationPick(StudioAllNameItemDto dto) {
		String name = LocalizationUtil.pickStudioName(
				dto.getNameKor(),
				dto.getNameEng()
		);
		return new StudioItemDto(dto.getStudioId(), name);
	}
}

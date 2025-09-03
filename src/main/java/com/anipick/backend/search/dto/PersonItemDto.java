package com.anipick.backend.search.dto;

import com.anipick.backend.common.util.LocalizationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PersonItemDto {
	private Long personId;
	private String name;
	private String profileImage;

	public static PersonItemDto personNameTranslationPick(PersonAllNameItemDto dto) {
		String name = LocalizationUtil.pickVoiceActorName(
				dto.getNameKor(),
				dto.getNameEng()
		);
		return new PersonItemDto(dto.getPersonId(), name, dto.getProfileImage());
	}
}

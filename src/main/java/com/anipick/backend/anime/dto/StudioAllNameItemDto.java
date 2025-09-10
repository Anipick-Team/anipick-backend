package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudioAllNameItemDto {
	private Long studioId;
	private String nameKor;
	private String nameEng;
}

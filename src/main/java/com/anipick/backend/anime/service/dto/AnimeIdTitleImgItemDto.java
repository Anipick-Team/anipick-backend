package com.anipick.backend.anime.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnimeIdTitleImgItemDto {
	private Long animeId;
	private String title;
	private String coverImageUrl;
}
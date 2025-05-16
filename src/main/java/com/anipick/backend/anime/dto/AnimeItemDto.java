package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnimeItemDto {
	private Long animeId;
	private String title;
	private String coverImageUrl;
}
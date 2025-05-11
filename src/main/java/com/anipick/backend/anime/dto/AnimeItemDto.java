package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnimeItemDto {
	private Long id;
	private String title;
	private String coverImageUrl;
}
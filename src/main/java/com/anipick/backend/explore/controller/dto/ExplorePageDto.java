package com.anipick.backend.explore.controller.dto;

import com.anipick.backend.anime.common.dto.AnimeItemDto;
import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ExplorePageDto {
	private Long count;
	private CursorDto cursor;
	private List<AnimeItemDto> animes;

	public static ExplorePageDto of(Long count, CursorDto cursor, List<AnimeItemDto> animes) {
		return new ExplorePageDto(count, cursor, animes);
	}
}
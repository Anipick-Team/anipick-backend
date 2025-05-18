package com.anipick.backend.search.dto;

import java.util.List;

import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.common.dto.CursorDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchAnimePageDto {
	private long count;
	private long personCount;
	private long studioCount;
	private CursorDto cursor;
	private List<AnimeItemDto> animes;
}

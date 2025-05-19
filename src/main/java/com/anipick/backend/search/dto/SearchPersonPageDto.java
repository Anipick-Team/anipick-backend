package com.anipick.backend.search.dto;

import java.util.List;

import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.common.dto.CursorDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchPersonPageDto {
	private long count;
	private long animeCount;
	private long studioCount;
	private CursorDto cursor;
	private List<PersonItemDto> persons;
}

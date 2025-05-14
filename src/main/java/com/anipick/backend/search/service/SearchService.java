package com.anipick.backend.search.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.search.dto.PersonItemDto;
import com.anipick.backend.search.dto.SearchAnimePageDto;
import com.anipick.backend.search.dto.SearchPersonPageDto;
import com.anipick.backend.search.mapper.SearchMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {
	private final SearchMapper mapper;

	public SearchAnimePageDto findSearchAnimes(String query, Long lastId, Long size) {
		String queryPattern = "%" + query + "%";

		long totalCount = mapper.countSearchAnime(queryPattern);

		List<AnimeItemDto> items = mapper.selectSearchAnimes(queryPattern, lastId, size);

		Long nextId;

		if (items.isEmpty()) {
			nextId = null;
		} else {
			nextId = items.getLast().getId();
		}

		CursorDto cursor = 	CursorDto.of(nextId);

		return new SearchAnimePageDto(totalCount, cursor, items);
	}

	public SearchPersonPageDto findSearchPersons(String query, Long lastId, Long size) {
		String queryPattern = "%" + query + "%";

		long totalCount = mapper.countSearchPerson(queryPattern);

		List<PersonItemDto> items = mapper.selectSearchPersons(queryPattern, lastId, size);

		Long nextId;

		if (items.isEmpty()) {
			nextId = null;
		} else {
			nextId = items.getLast().getPersonId();
		}

		CursorDto cursor = 	CursorDto.of(nextId);

		return new SearchPersonPageDto(totalCount, cursor, items);
	}
}

package com.anipick.backend.search.service;

import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.search.dto.*;
import com.anipick.backend.search.mapper.SearchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
	private final SearchMapper mapper;
	private static final String LOG_BASE_URL = "http://anipick.p-e.kr:8080/api/log/";

	public SearchInitPageDto findWeekBestAnimes() {
		LocalDate now = LocalDate.now();
		List<AnimeItemDto> items = mapper.selectSearchWeekBestAnimes(now)
				.stream()
				.map(AnimeItemDto::animeTitleTranslationPick)
				.toList();
		return new SearchInitPageDto(items);
	}

	public SearchAnimePageDto findSearchAnimes(String query, Long lastId, Long size, Long page) {

		long nextPage = page + 1;
		long totalCount = mapper.countSearchAnime(query);
		long personCount = mapper.countSearchPerson(query);
		long studioCount = mapper.countSearchStudio(query);

		List<AnimeItemDto> items = mapper.selectSearchAnimes(query, lastId, size)
				.stream()
				.map(AnimeItemDto::animeTitleTranslationPick)
				.toList();

		int positionNumber = (int) ((page - 1) * size);

		List<SearchLogAnimeItemDto> animeLogItems = new ArrayList<>(18);

		for (AnimeItemDto item : items) {
			positionNumber++;
			animeLogItems.add(
					SearchLogAnimeItemDto.from(item, positionNumber, LOG_BASE_URL, query)
			);
		}

		Long nextId;

		if (items.isEmpty()) {
			nextId = null;
		} else {
			nextId = items.getLast().getAnimeId();
		}

		CursorDto cursor = CursorDto.of(nextId);

		return new SearchAnimePageDto(totalCount, nextPage, personCount, studioCount, cursor, animeLogItems);
	}

	public SearchPersonPageDto findSearchPersons(String query, Long lastId, Long size) {
		long totalCount = mapper.countSearchPerson(query);
		long animeCount = mapper.countSearchAnime(query);
		long studioCount = mapper.countSearchStudio(query);

		List<PersonItemDto> items = mapper.selectSearchPersons(query, lastId, size);

		Long nextId;

		if (items.isEmpty()) {
			nextId = null;
		} else {
			nextId = items.getLast().getPersonId();
		}

		CursorDto cursor = CursorDto.of(nextId);

		return new SearchPersonPageDto(totalCount, animeCount, studioCount, cursor, items);
	}

	public SearchStudioPageDto findSearchStudios(String query, Long lastId, Long size) {
		long totalCount = mapper.countSearchStudio(query);
		long animeCount = mapper.countSearchAnime(query);
		long personCount = mapper.countSearchPerson(query);

		List<StudioItemDto> items = mapper.selectSearchStudios(query, lastId, size);

		Long nextId;

		if (items.isEmpty()) {
			nextId = null;
		} else {
			nextId = items.getLast().getStudioId();
		}

		CursorDto cursor = CursorDto.of(nextId);

		return new SearchStudioPageDto(totalCount, animeCount, personCount, cursor, items);
	}
}

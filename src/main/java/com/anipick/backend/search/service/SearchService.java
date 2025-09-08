package com.anipick.backend.search.service;

import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.search.dto.*;
import com.anipick.backend.search.mapper.SearchMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
	private final SearchMapper mapper;
	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	@Value("${log.base-url}")
	private String LOG_BASE_URL;

	@Value("${anime.week-best-key}")
	private String ANIME_WEEK_BEST_REDIS_KEY;

	public SearchInitPageDto findWeekBestAnimes() {
		String redisWeekBestAnimeIdsJsonStr = redisTemplate.opsForValue().get(ANIME_WEEK_BEST_REDIS_KEY);
		try {
			JsonNode jsonNode = objectMapper.readTree(redisWeekBestAnimeIdsJsonStr);
			List<Long> searchBestAnimeIds = objectMapper
				.convertValue(jsonNode.get("search_anime_id"), new TypeReference<>() {});

			List<AnimeItemDto> items = mapper.selectSearchWeekBestAnimes(searchBestAnimeIds)
				.stream()
				.map(AnimeItemDto::animeTitleTranslationPick)
				.toList();

			return new SearchInitPageDto(items);

		} catch (JsonProcessingException e) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
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

		List<PersonItemDto> items = mapper.selectSearchPersons(query, lastId, size)
				.stream()
				.map(PersonItemDto::personNameTranslationPick)
				.toList();

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

		List<StudioItemDto> items = mapper.selectSearchStudios(query, lastId, size)
				.stream()
				.map(StudioItemDto::studioNameTranslationPick)
				.toList();

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

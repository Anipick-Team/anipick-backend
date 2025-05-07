package com.anipick.backend.explore.service;

import com.anipick.backend.anime.common.dto.AnimeItemDto;
import com.anipick.backend.anime.common.util.FormatConvert;
import com.anipick.backend.common.domain.SortOption;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.explore.domain.GenresOption;
import com.anipick.backend.explore.dto.ExploreItemDto;
import com.anipick.backend.explore.dto.ExplorePageDto;
import com.anipick.backend.explore.mapper.ExploreMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExploreService {
	private final ExploreMapper mapper;

	public ExplorePageDto explore(
		Integer year, Integer season,
		List<Long> genres, GenresOption genreOp,
		String type, String sort,
		Long lastId, Integer lastValue,
		int size
	) {
		log.debug(
			"Anime Explore log : 년도={}, 분기={}, 장르 ID={}, 장르 옵션={}, 타입={}, 정렬={}, 마지막 ID={}, 마지막 값={}, 페이지 크기={}",
			year, season, genres, genreOp, type, sort, lastId, lastValue, size
		);
		int genresSize = genres == null ? 0 : genres.size();

		List<String> convert = FormatConvert.toConvert(type);
		int typeConvertSize = convert.size();

		SortOption sortOption = SortOption.of(sort);
		String orderByQuery = sortOption.getOrderByQuery();

		String genreOpName = genreOp.name();

		long total = mapper.countExplored(
			year, season,
			genres, genresSize,
			convert, typeConvertSize,
			genreOpName, type);

		List<ExploreItemDto> internal = mapper.selectExplored(
			year, season,
			genres, genresSize,
			convert, typeConvertSize,
			genreOpName, type,
			sort, orderByQuery,
			lastId, lastValue,
			size);


		int lastIndex = internal.size() - 1;

		Long nextId;
		if (internal.isEmpty()) {
			nextId = null;
		} else {
			nextId = internal.get(lastIndex).getId();
		}

		Integer nextValue;
		if (!"rating".equalsIgnoreCase(sort) || internal.isEmpty()) {
			nextValue = null;
		} else {
			nextValue = internal.get(lastIndex).getAverageScore();
		}

		String nextValStr;
		if (nextValue != null) {
			nextValStr = nextValue.toString();
		} else {
			nextValStr = null;
		}

		CursorDto cursor = CursorDto.of(sort, nextId, nextValStr);

		List<AnimeItemDto> responseItems = internal.stream()
			.map(e -> new AnimeItemDto(e.getId(), e.getTitle(), e.getCoverImageUrl()))
			.collect(Collectors.toList());

		return ExplorePageDto.of(total, cursor, responseItems);
	}
}

package com.anipick.backend.explore.service;

import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.anime.util.FormatConvert;
import com.anipick.backend.anime.domain.RangeDate;
import com.anipick.backend.anime.domain.SeasonConverter;
import com.anipick.backend.common.domain.SortOption;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.explore.domain.GenresOption;
import com.anipick.backend.explore.dto.ExploreItemDto;
import com.anipick.backend.explore.dto.ExplorePageDto;
import com.anipick.backend.explore.dto.ExploreRequestDto;
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
		Long lastId, Double lastValue,
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

        ExploreRequestDto exploreRequestDto = makeExploreRequestDto(
				year, season, genres, type, sort, lastId, lastValue, size,
				genresSize, genreOpName, convert, typeConvertSize, orderByQuery);

        long total = mapper.countExplored(exploreRequestDto);

        List<ExploreItemDto> internal = mapper.selectExplored(exploreRequestDto);

		int lastIndex = internal.size() - 1;

		Long nextId;
		if (internal.isEmpty()) {
			nextId = null;
		} else if ("popularity".equalsIgnoreCase(sort)){
			nextId = internal.get(lastIndex).getPopularId();
		} else {
			nextId = internal.get(lastIndex).getId();
		}

		Double nextValue;
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

	private static ExploreRequestDto makeExploreRequestDto(Integer year, Integer season, List<Long> genres, String type,
        String sort, Long lastId, Double lastValue, int size, int genresSize, String genreOpName, List<String> convert,
        int typeConvertSize, String orderByQuery) {
        if (year != null && season != null) {
            RangeDate dateRange = SeasonConverter.getRangDate(year, season);
            return ExploreRequestDto.of(
                dateRange,
                genres,
                genresSize,
                genreOpName,
                convert,
                typeConvertSize,
                type,
                sort,
                orderByQuery,
                lastId,
                lastValue,
                size
            );
        } else if (year != null) {
            RangeDate dateRange = SeasonConverter.getYearRangDate(year);
            return ExploreRequestDto.of(
                dateRange,
                genres,
                genresSize,
                genreOpName,
                convert,
                typeConvertSize,
                type,
                sort,
                orderByQuery,
                lastId,
                lastValue,
                size
            );
        }
        return ExploreRequestDto.dateNullOf(
            genres,
            genresSize,
            genreOpName,
            convert,
            typeConvertSize,
            type,
            sort,
            orderByQuery,
            lastId,
            lastValue,
            size
        );
    }
}

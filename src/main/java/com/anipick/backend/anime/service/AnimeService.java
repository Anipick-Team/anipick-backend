package com.anipick.backend.anime.service;

import com.anipick.backend.anime.dto.*;
import com.anipick.backend.anime.domain.RangeDate;
import com.anipick.backend.anime.domain.Season;
import com.anipick.backend.anime.domain.SeasonConverter;
import com.anipick.backend.anime.mapper.AnimeMapper;

import com.anipick.backend.common.domain.SortOption;
import com.anipick.backend.common.dto.CursorDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnimeService {
	private final AnimeMapper mapper;
	@Value("${anime.default-cover-url}")
	private String defaultCoverUrl;

	public UpcomingSeasonResultDto getUpcomingSeasonAnimes() {
		LocalDate now = LocalDate.now();
		Season nextSeason = Season.getNextSeason(now);
		RangeDate nextSeasonRangDate = SeasonConverter.getNextSeasonRangDate(now);

		String startDate = nextSeasonRangDate.getStartDate();
		String endDate = nextSeasonRangDate.getEndDate();

		int season = nextSeason.getCode();
		String yearSubString = startDate.substring(0, 4);
		int seasonYear = Integer.parseInt(yearSubString);

		RangeDateRequestDto rangeDateRequestDto = RangeDateRequestDto
			.builder()
			.startDate(startDate)
			.endDate(endDate)
			.build();

		List<AnimeIdTitleImgItemDto> nextSeasonAnimes =
			mapper.selectUpcomingSeasonAnimes(rangeDateRequestDto);

		Collections.shuffle(nextSeasonAnimes);

		if (nextSeasonAnimes.size() > 10) {
			List<AnimeIdTitleImgItemDto> animes10SubList = nextSeasonAnimes.subList(0, 10);
			return UpcomingSeasonResultDto.of(season, seasonYear, animes10SubList);
		}
		return UpcomingSeasonResultDto.of(season, seasonYear, nextSeasonAnimes);
	}

	public ComingSoonPageDto getComingSoonAnimes(
			String sort, Long lastId, Long size, Long includeAdult, String lastValue
	) {
		SortOption sortOption = SortOption.of(sort);
		String orderByQuery = sortOption.getOrderByQuery();

		ComingSoonRequestDto comingSoonRequestDto =
				ComingSoonRequestDto.of(lastId, lastValue, size, includeAdult, orderByQuery, defaultCoverUrl);

		long totalCount = mapper.countComingSoon(comingSoonRequestDto);


        switch (sortOption) {
			case LATEST -> {
                return getSortLatestComingSoonAnimes(sort, comingSoonRequestDto, totalCount);
            }
			case POPULARITY -> {
                return getSortPopularityComingSoonAnimes(sort, comingSoonRequestDto, totalCount);
            }
            default -> {
                return getSortStartDateComingSoonAnimes(sort, comingSoonRequestDto, totalCount);
            }
        }
	}

	private ComingSoonPageDto getSortLatestComingSoonAnimes(String sort, ComingSoonRequestDto comingSoonRequestDto, long totalCount) {
		List<ComingSoonItemBasicDto> latestSortAnimes =
				mapper.selectComingSoonLatestAnimes(comingSoonRequestDto);
		List<ComingSoonItemBasicDto> imgFilterItems = latestSortAnimes.stream()
				.map(ComingSoonItemBasicDto::typeToReleaseDate)
				.collect(Collectors.toList());

		List<ComingSoonItemDto> items = imgFilterItems.stream()
				.map(b -> new ComingSoonItemDto(
						b.getAnimeId(),
						b.getTitle(),
						b.getCoverImageUrl(),
						b.getStartDate(),
						b.getIsAdult()
				))
				.collect(Collectors.toList());

		Long nextId;

		if (items.isEmpty()) {
			nextId = null;
		} else {
			nextId = items.getLast().getAnimeId();
		}

		CursorDto cursor = CursorDto.of(sort, nextId, null);
		return ComingSoonPageDto.of(totalCount, cursor, items);
	}

	private ComingSoonPageDto getSortPopularityComingSoonAnimes(String sort, ComingSoonRequestDto comingSoonRequestDto, long totalCount) {
		List<ComingSoonItemPopularityDto> popularitySortAnimes =
				mapper.selectComingSoonPopularityAnimes(comingSoonRequestDto);
		List<ComingSoonItemPopularityDto> imgFilterItems = popularitySortAnimes.stream()
				.map(ComingSoonItemPopularityDto::typeToReleaseDate)
				.collect(Collectors.toList());

		Long nextId;

		if (imgFilterItems.isEmpty()) {
			nextId = null;
		} else {
			nextId = imgFilterItems.getLast().getPopularId();
		}

		List<ComingSoonItemDto> items = imgFilterItems.stream()
				.map(b -> new ComingSoonItemDto(
						b.getAnimeId(),
						b.getTitle(),
						b.getCoverImageUrl(),
						b.getStartDate(),
						b.getIsAdult()
				))
				.collect(Collectors.toList());

		CursorDto cursor = CursorDto.of(sort, nextId, null);
		return ComingSoonPageDto.of(totalCount, cursor, items);
	}

	private ComingSoonPageDto getSortStartDateComingSoonAnimes(String sort, ComingSoonRequestDto comingSoonRequestDto, long totalCount) {
		List<ComingSoonItemBasicDto> starDateSortAnimes =
				mapper.selectComingSoonStartDateAnimes(comingSoonRequestDto);
		List<ComingSoonItemBasicDto> imgFilterItems = starDateSortAnimes.stream()
				.collect(Collectors.toList());

		String nextValue;

		if (imgFilterItems.isEmpty()) {
			nextValue = null;
		} else {
			nextValue = imgFilterItems.getLast().getStartDate();
		}

		List<ComingSoonItemBasicDto> typeCovertAnimes = imgFilterItems.stream()
				.map(ComingSoonItemBasicDto::typeToReleaseDate)
				.collect(Collectors.toList());

		List<ComingSoonItemDto> items = typeCovertAnimes.stream()
				.map(b -> new ComingSoonItemDto(
						b.getAnimeId(),
						b.getTitle(),
						b.getCoverImageUrl(),
						b.getStartDate(),
						b.getIsAdult()
				))
				.collect(Collectors.toList());

		Long nextId;

		if (starDateSortAnimes.isEmpty()) {
			nextId = null;
		} else {
			nextId = imgFilterItems.getLast().getAnimeId();
		}
		CursorDto cursor = CursorDto.of(sort, nextId, nextValue);
		return ComingSoonPageDto.of(totalCount, cursor, items);
	}

	public List<AnimeCharacterActorItemDto> getAnimeInfoCharacterActor(Long animeId) {
		List<AnimeCharacterActorItemDto> items = mapper.selectAnimeInfoCharacterActors(animeId, 10);
		return items;
	}
}
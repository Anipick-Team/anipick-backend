package com.anipick.backend.anime.service;

import com.anipick.backend.anime.dto.*;
import com.anipick.backend.anime.domain.RangeDate;
import com.anipick.backend.anime.domain.Season;
import com.anipick.backend.anime.domain.SeasonConverter;
import com.anipick.backend.anime.mapper.AnimeMapper;

import com.anipick.backend.common.domain.SortOption;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.anime.dto.AnimeDetailInfoReviewsPageDto;
import com.anipick.backend.anime.dto.AnimeDetailInfoReviewsRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnimeService {
	private final AnimeMapper mapper;
	@Value("${anime.default-cover-url}")
	private String defaultCoverUrl;

	private static final DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd");

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

	public AnimeDetailInfoReviewsPageDto getAnimeInfoReviews(Long animeId, Long userId, String sort, Boolean isSpoiler, Long lastId, String lastValue, int size) {
		SortOption sortOption = SortOption.of(sort);
		String orderByQuery = sortOption.getOrderByQuery();

		AnimeDetailInfoReviewsRequestDto reviewsRequestDto =
				AnimeDetailInfoReviewsRequestDto.of(animeId, userId, sort, orderByQuery, isSpoiler, lastId, lastValue, size);

		long totalCount = mapper.selectAnimeReviewCount(animeId);

		List<AnimeDetailInfoReviewsItemDto> raws = mapper.selectAnimeDetailInfoReviews(reviewsRequestDto);

		List<AnimeDetailInfoReviewsResultDto> convertCreatedAtItems = raws.stream()
				.map(dto -> {
					LocalDateTime dateTime = LocalDateTime.parse(dto.getCreatedAt(), parser);
					String formattedDate = dateTime.format(formatter);

					return AnimeDetailInfoReviewsResultDto.of(
							dto.getReviewId(),
							dto.getNickname(),
							dto.getProfileImageUrl(),
							dto.getRating(),
							dto.getContent(),
							formattedDate,
							dto.getIsSpoiler(),
							dto.getLikeCount(),
							dto.getIsLiked(),
							dto.getIsMine()
					);
				})
				.toList();
		switch (sortOption) {
			// 최신 순
			case LATEST -> {
				return getSortLatestAnimeDetailReviews(totalCount, convertCreatedAtItems, sort);
			}
			// 평점 높은 순
			case RATING_DESC -> {
				return getSortRatingDescAnimeDetailReviews(totalCount, convertCreatedAtItems, sort);
			}
			// 평점 낮은 순
			case RATING_ASC -> {
				return getSortRatingAscAnimeDetailReviews(totalCount, convertCreatedAtItems, sort);
			}
			// 좋아요 순
			default -> {
				return getSortLikesAnimeDetailReviews(totalCount, convertCreatedAtItems, sort);
			}
		}
	}

	private AnimeDetailInfoReviewsPageDto getSortLatestAnimeDetailReviews(long totalCount, List<AnimeDetailInfoReviewsResultDto> items, String sort) {
		Long nextId;

		if (items.isEmpty()) {
			nextId = null;
		} else {
			nextId = items.getLast().getReviewId();
		}

		CursorDto cursor = CursorDto.of(sort, nextId);

		return AnimeDetailInfoReviewsPageDto.of(totalCount, cursor, items);
	}

	private AnimeDetailInfoReviewsPageDto getSortRatingDescAnimeDetailReviews(long totalCount, List<AnimeDetailInfoReviewsResultDto> items, String sort) {
		Long nextId;
		Double nextValue;

		if (items.isEmpty()) {
			nextId = null;
			nextValue = null;
		} else {
			nextId = items.getLast().getReviewId();
			nextValue = items.getLast().getRating();
		}

		String nextValueStr;
		if (nextValue == null) {
			nextValueStr = null;
		} else {
			nextValueStr = nextValue.toString();
		}

		CursorDto cursor = CursorDto.of(sort, nextId, nextValueStr);

		return AnimeDetailInfoReviewsPageDto.of(totalCount, cursor, items);
	}

	private AnimeDetailInfoReviewsPageDto getSortRatingAscAnimeDetailReviews(long totalCount, List<AnimeDetailInfoReviewsResultDto> items, String sort) {
		Long nextId;
		Double nextValue;

		if (items.isEmpty()) {
			nextId = null;
			nextValue = null;
		} else {
			nextId = items.getLast().getReviewId();
			nextValue = items.getLast().getRating();
		}

		String nextValueStr;
		if (nextValue == null) {
			nextValueStr = null;
		} else {
			nextValueStr = nextValue.toString();
		}

		CursorDto cursor = CursorDto.of(sort, nextId, nextValueStr);

		return AnimeDetailInfoReviewsPageDto.of(totalCount, cursor, items);
	}

	private AnimeDetailInfoReviewsPageDto getSortLikesAnimeDetailReviews(long totalCount, List<AnimeDetailInfoReviewsResultDto> items, String sort) {
		Long nextId;
		Long nextValue;

		if (items.isEmpty()) {
			nextId = null;
			nextValue = null;
		} else {
			nextId = items.getLast().getReviewId();
			nextValue = items.getLast().getLikeCount();
		}

		String nextValueStr;
		if (nextValue == null) {
			nextValueStr = null;
		} else {
			nextValueStr = nextValue.toString();
		}

		CursorDto cursor = CursorDto.of(sort, nextId, nextValueStr);

		return AnimeDetailInfoReviewsPageDto.of(totalCount, cursor, items);
	}
}
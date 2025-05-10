package com.anipick.backend.anime.service;

import com.anipick.backend.anime.controller.dto.UpcomingSeasonResultDto;
import com.anipick.backend.anime.domain.RangeDate;
import com.anipick.backend.anime.domain.Season;
import com.anipick.backend.anime.domain.SeasonConverter;
import com.anipick.backend.anime.mapper.AnimeMapper;
import com.anipick.backend.anime.service.dto.AnimeIdTitleImgItemDto;
import com.anipick.backend.anime.service.dto.RangeDateRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnimeService {
	private final AnimeMapper mapper;

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
}
package com.anipick.backend.anime.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpcomingSeasonResultDto {
	private Integer season;
	private Integer seasonYear;
	private List<AnimeIdTitleImgItemDto> animes;

	public static UpcomingSeasonResultDto of(Integer season, Integer seasonYear, List<AnimeIdTitleImgItemDto> animes) {
		return new UpcomingSeasonResultDto(season, seasonYear, animes);
	}
}

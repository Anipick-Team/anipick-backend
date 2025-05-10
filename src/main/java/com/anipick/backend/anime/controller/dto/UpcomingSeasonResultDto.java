package com.anipick.backend.anime.controller.dto;

import java.util.List;

import com.anipick.backend.anime.service.dto.AnimeIdTitleImgItemDto;

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

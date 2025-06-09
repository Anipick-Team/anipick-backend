package com.anipick.backend.recommendation.dto;

import java.util.List;

import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.common.dto.CursorDto;

import com.anipick.backend.test.TestAnimeItemDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserMainRecommendationPageDto {
	private Long count;
	private CursorDto cursor;
	private List<TestAnimeItemDto> referenceAnimes;
	private List<TestAnimeItemDto> animes;

	public UserMainRecommendationPageDto(Long count, CursorDto cursor, List<TestAnimeItemDto> animes) {
		this.count = count;
		this.cursor = cursor;
		this.animes = animes;
	}

	public static UserMainRecommendationPageDto of(Long count, CursorDto cursor, List<TestAnimeItemDto> referenceAnimes, List<TestAnimeItemDto> animes) {
		return new UserMainRecommendationPageDto(count, cursor, referenceAnimes, animes);
	}

	public static UserMainRecommendationPageDto emptyReviewOf(Long count, List<TestAnimeItemDto> animes) {
		return new UserMainRecommendationPageDto(count, null, animes);
	}
}

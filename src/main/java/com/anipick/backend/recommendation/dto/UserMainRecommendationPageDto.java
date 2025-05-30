package com.anipick.backend.recommendation.dto;

import java.util.List;

import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.common.dto.CursorDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserMainRecommendationPageDto {
	private Long count;
	private CursorDto cursor;
	private List<AnimeItemDto> animes;

	public static UserMainRecommendationPageDto of(Long count, CursorDto cursor, List<AnimeItemDto> animes) {
		return new UserMainRecommendationPageDto(count, cursor, animes);
	}

	public static UserMainRecommendationPageDto emptyReviewOf(Long count, List<AnimeItemDto> animes) {
		return new UserMainRecommendationPageDto(count, null, animes);
	}
}

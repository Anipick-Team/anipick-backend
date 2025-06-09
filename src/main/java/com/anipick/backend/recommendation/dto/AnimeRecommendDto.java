package com.anipick.backend.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnimeRecommendDto {
	private Long  animeId;
	private String title;
	private String coverImageUrl;
	private Long  score;
}

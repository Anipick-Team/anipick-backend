package com.anipick.backend.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnimeRecommendDto2 {
	private final Long  animeId;
	private final String title;
	private final String coverImageUrl;
	private final Long  score;
	private final Long tagCount;
}

package com.anipick.backend.recommendation.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TagBasedRequestDto {
	private Long userId;
	private List<Long> topAnimeIds;
	private Long lastScore;
	private Long lastAnimeId;
	private Long size;
}

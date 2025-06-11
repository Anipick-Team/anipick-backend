package com.anipick.backend.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecentHighRequestDto {
	private Long userId;
	private Long referenceAnimeId;
	private Long lastScore;
	private Long lastAnimeId;
	private Long size;
}

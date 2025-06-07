package com.anipick.backend.recommendation.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecentHighFullRequest {
	private Long userId;
	private Long referenceAnimeId;
	private List<Long> tagIds;
	private Long lastScore;
	private Long lastCount;
	private Long lastId;
	private Long size;
}
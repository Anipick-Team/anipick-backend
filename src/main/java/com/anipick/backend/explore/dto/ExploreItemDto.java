package com.anipick.backend.explore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExploreItemDto {
	private Long    id;
	private String  title;
	private String  coverImageUrl;
	private Double averageScore;
	private Long popularId;
}

package com.anipick.backend.test;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TestAnimeItemDto {
	private Long animeId;
	private String title;
	private String coverImageUrl;
	private List<TestAnimeTagDto> tags;
}
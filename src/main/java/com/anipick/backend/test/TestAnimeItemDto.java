package com.anipick.backend.test;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TestAnimeItemDto {
	private Long animeId;
	private String title;
	private String coverImageUrl;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Double rating;
	private List<TestAnimeTagDto> tags;
}
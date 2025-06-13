package com.anipick.backend.test;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnimeItemDto2 {
    private Long animeId;
	private String title;
	private String coverImageUrl;
    private Double rating;
}

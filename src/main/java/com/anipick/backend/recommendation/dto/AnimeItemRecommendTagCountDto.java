package com.anipick.backend.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnimeItemRecommendTagCountDto {
    private Long animeId;
    private String title;
    private String coverImage;
    private Long tagCount;
}

package com.anipick.backend.home.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class HomeRecommendationItemDto {
    private String referenceAnimeTitle;
    private List<HomeRecommendationAnimeItemDto> animes;
}

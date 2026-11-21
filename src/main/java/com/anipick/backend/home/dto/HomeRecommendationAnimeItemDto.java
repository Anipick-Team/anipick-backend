package com.anipick.backend.home.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HomeRecommendationAnimeItemDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private Double averageRating;
    private List<String> genres;
    private Long nextEpisode;
}

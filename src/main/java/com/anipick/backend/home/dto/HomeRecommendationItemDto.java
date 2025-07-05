package com.anipick.backend.home.dto;

import com.anipick.backend.anime.dto.AnimeItemDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class HomeRecommendationItemDto {
    private String referenceAnimeTitle;
    private List<AnimeItemDto> animes;
}

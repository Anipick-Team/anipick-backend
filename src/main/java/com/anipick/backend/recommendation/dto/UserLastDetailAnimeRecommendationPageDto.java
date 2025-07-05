package com.anipick.backend.recommendation.dto;

import com.anipick.backend.anime.dto.AnimeItemDto;
import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class UserLastDetailAnimeRecommendationPageDto {
    private String standardAnimeTitle;
    private CursorDto cursor;
    private List<AnimeItemDto> animes;
}

package com.anipick.backend.anime.dto;

import com.anipick.backend.common.dto.CursorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class AnimeRecommendationPageDto {
    private String referenceAnimeTitle;
    private CursorDto cursor;
    private List<AnimeItemDto> animes;
}

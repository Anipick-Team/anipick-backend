package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DayOfWeekAnimeItemDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private List<String> genres;
    private Long nextEpisode;
}

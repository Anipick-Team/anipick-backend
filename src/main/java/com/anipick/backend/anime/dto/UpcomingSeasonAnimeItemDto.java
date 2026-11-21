package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UpcomingSeasonAnimeItemDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private List<String> genres;
}

package com.anipick.backend.anime.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class AnimeWeekScheduleDto {
    private Long animeId;
    private String titleKor;
    private String coverImageUrl;
    private List<String> genres;
    private String airingAt;
    private Integer episode;
    private Integer popularityRank;
    private Integer latestRank;

    public static AnimeItemDto toAnimeItem(AnimeWeekScheduleDto dto) {
        return new AnimeItemDto(dto.animeId, dto.titleKor, dto.coverImageUrl);
    }
}

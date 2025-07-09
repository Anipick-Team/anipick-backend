package com.anipick.backend.ranking.dto;

import com.anipick.backend.anime.dto.GenreDto;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class RealTimeRankingAnimesFromQueryDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private List<GenreDto> genres;
    private LocalDate rankDate;
}

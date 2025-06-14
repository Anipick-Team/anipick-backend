package com.anipick.backend.ranking.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class RankingAnimesFromQueryDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private Long rank;
    private List<String> genres;
    private LocalDate rankDate;
}

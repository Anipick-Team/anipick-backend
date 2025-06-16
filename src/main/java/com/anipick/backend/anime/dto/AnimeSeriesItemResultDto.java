package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class AnimeSeriesItemResultDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private String airDate;
}

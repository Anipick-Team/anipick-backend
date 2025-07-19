package com.anipick.backend.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnimeGenresDto {
    private Long animeId;
    private Long genreId;
    private String genreName;
}

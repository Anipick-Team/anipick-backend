package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnimeGenreDto {
    private Long animeId;
    private Long genreId;
    private String genreName;
}

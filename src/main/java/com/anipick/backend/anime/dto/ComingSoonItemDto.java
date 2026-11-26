package com.anipick.backend.anime.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ComingSoonItemDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private String releaseDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long popularId;
    private Boolean isAdult;
    private List<String> genres;

    public ComingSoonItemDto(Long animeId, String title, String coverImageUrl, String releaseDate, Boolean isAdult, List<String> genres) {
        this.animeId = animeId;
        this.title = title;
        this.coverImageUrl = coverImageUrl;
        this.releaseDate = releaseDate;
        this.isAdult = isAdult;
        this.genres = genres;
    }
}

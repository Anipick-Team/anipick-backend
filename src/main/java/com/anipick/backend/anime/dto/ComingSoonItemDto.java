package com.anipick.backend.anime.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

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

    public ComingSoonItemDto(Long animeId, String title, String coverImageUrl, String releaseDate, Boolean isAdult) {
        this.animeId = animeId;
        this.title = title;
        this.coverImageUrl = coverImageUrl;
        this.releaseDate = releaseDate;
        this.isAdult = isAdult;
    }
}

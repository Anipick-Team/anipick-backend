package com.anipick.backend.anime.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AnimeDateItemDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private LocalDate startDate;
}

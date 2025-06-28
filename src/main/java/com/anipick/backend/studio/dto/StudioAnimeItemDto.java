package com.anipick.backend.studio.dto;

import lombok.Getter;

@Getter
public class StudioAnimeItemDto {
    private Long animeId;
    private String title;
    private String coverImageUrl;
    private Long seasonYear;
}

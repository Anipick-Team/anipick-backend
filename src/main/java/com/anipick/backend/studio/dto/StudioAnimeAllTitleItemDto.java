package com.anipick.backend.studio.dto;

import lombok.Getter;

@Getter
public class StudioAnimeAllTitleItemDto {
    private Long animeId;
    private String titleKor;
    private String titleEng;
    private String titleRom;
    private String titleNat;
    private String coverImageUrl;
    private Long seasonYear;
}

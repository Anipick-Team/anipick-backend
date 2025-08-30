package com.anipick.backend.anime.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AnimeAlltitleDateDto {
    private Long animeId;
    private String titleKor;
	private String titleEng;
	private String titleRom;
	private String titleNat;
    private String coverImageUrl;
    private LocalDate startDate;
}

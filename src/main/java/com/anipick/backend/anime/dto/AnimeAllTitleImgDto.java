package com.anipick.backend.anime.dto;

import lombok.Getter;

@Getter
public class AnimeAllTitleImgDto {
    private Long animeId;
	private String titleKor;
	private String titleEng;
	private String titleRom;
	private String titleNat;
	private String coverImageUrl;
}

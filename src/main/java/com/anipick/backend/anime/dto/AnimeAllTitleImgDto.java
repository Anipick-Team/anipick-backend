package com.anipick.backend.anime.dto;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public class AnimeAllTitleImgDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long animeId;
	private String titleKor;
	private String titleEng;
	private String titleRom;
	private String titleNat;
	private String coverImageUrl;
}

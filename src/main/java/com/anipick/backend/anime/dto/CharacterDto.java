package com.anipick.backend.anime.dto;

import lombok.Getter;

@Getter
public class CharacterDto {
    private Long id;
    private String nameKor;
    private String nameEng;
    private String imageUrl;
}

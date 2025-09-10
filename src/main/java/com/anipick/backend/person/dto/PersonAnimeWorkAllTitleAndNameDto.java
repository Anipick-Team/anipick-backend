package com.anipick.backend.person.dto;

import lombok.Getter;

@Getter
public class PersonAnimeWorkAllTitleAndNameDto {
    private Long animeId;
    private String animeTitleKor;
    private String animeTitleEng;
    private String animeTitleRom;
    private String animeTitleNat;
    private Long characterId;
    private String characterNameKor;
    private String characterNameEng;
    private String characterImageUrl;
}
